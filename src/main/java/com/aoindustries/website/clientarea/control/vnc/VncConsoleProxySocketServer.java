/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.vnc;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Brand;
import com.aoindustries.aoserv.client.NetBind;
import com.aoindustries.website.LogFactory;
import com.aoindustries.website.SiteSettings;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Collections;
import java.util.logging.Level;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyStoreBuilderParameters;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.servlet.ServletContext;

/**
 * Listens on SSL socket for incoming connections and proxies through to the
 * behind-the-scenes VNC server.
 *
 * @author  AO Industries, Inc.
 */
public class VncConsoleProxySocketServer implements Runnable {

	private ServletContext servletContext;
	private volatile Thread thread;

	public void init(ServletContext servletContext) {
		this.servletContext = servletContext;
		(thread = new Thread(this, "VNC Console Proxy Socket Server")).start();
	}

	public void destroy() {
		Thread T = this.thread;
		if(T!=null) {
			this.thread = null;
			T.interrupt();
		}
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		ServletContext myServletContext = this.servletContext;
		while(currentThread==this.thread) {
			try {
				SiteSettings siteSettings = SiteSettings.getInstance(myServletContext);
				Brand brand = siteSettings.getBrand();
				NetBind vncBind = brand.getAowebStrutsVncBind();
				InetAddress inetAddress = InetAddress.getByName(vncBind.getIPAddress().getInetAddress().toString());
				AOServConnector rootConn = siteSettings.getRootAOServConnector();
				// Init SSL without using system properties because default SSLContext may be already set
				// From: http://java.sun.com/j2se/1.5.0/docs/guide/security/jsse/JSSERefGuide.html  "Multiple and Dynamic Keystores"
				KeyStore.Builder fsBuilder = KeyStore.Builder.newInstance(
					brand.getAowebStrutsKeystoreType(),
					null,
					new File(myServletContext.getRealPath("/WEB-INF/keystore")),
					new KeyStore.PasswordProtection(brand.getAowebStrutsKeystorePassword().toCharArray())
				);
				ManagerFactoryParameters ksParams = new KeyStoreBuilderParameters(Collections.singletonList(fsBuilder));
				KeyManagerFactory factory = KeyManagerFactory.getInstance("NewSunX509");
				factory.init(ksParams);
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(factory.getKeyManagers(), null, null);

				// Create the server socket
				SSLServerSocketFactory socketFactory = ctx.getServerSocketFactory();
				//SSLServerSocketFactory socketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
				SSLServerSocket SS=(SSLServerSocket)socketFactory.createServerSocket(vncBind.getPort().getPort(), 50, inetAddress);
				try {
					while(currentThread==this.thread) {
						Socket socket = SS.accept();
						socket.setKeepAlive(true);
						new VncConsoleProxySocketHandler(servletContext, rootConn, socket);
					}
				} finally {
					SS.close();
				}
			} catch(ThreadDeath TD) {
				throw TD;
			} catch(Throwable T) {
				LogFactory.getLogger(myServletContext, VncConsoleProxySocketServer.class).log(Level.SEVERE, null, T);
			}
			try {
				Thread.sleep(60000);
			} catch(InterruptedException err) {
				LogFactory.getLogger(myServletContext, VncConsoleProxySocketServer.class).log(Level.WARNING, null, err);
				// Restore the interrupted status
				Thread.currentThread().interrupt();
			}
		}
	}
}
