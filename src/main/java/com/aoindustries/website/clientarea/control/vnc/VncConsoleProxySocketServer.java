/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2018, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoweb-struts-core.
 *
 * aoweb-struts-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoweb-struts-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoweb-struts-core.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.website.clientarea.control.vnc;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.website.LogFactory;
import com.aoindustries.website.SiteSettings;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
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
		if(!"false".equals(servletContext.getInitParameter(VncConsoleProxySocketServer.class.getName() + ".enabled"))) {
			(thread = new Thread(this, "VNC Console Proxy Socket Host")).start();
		}
	}

	public void destroy() {
		Thread T = this.thread;
		if(T != null) {
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
				Bind vncBind = brand.getAowebStrutsVncBind();
				InetAddress inetAddress = InetAddress.getByName(vncBind.getIpAddress().getInetAddress().toString());
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
				try (SSLServerSocket SS = (SSLServerSocket)socketFactory.createServerSocket(vncBind.getPort().getPort(), 50, inetAddress)) {
					while(currentThread==this.thread) {
						Socket socket = SS.accept();
						socket.setKeepAlive(true);
						new VncConsoleProxySocketHandler(servletContext, rootConn, socket);
					}
				}
			} catch(RuntimeException | IOException | InvalidAlgorithmParameterException | KeyManagementException | NoSuchAlgorithmException | SQLException T) {
				LogFactory.getLogger(myServletContext, VncConsoleProxySocketServer.class).log(Level.SEVERE, null, T);
			}
			if(currentThread==this.thread) {
				try {
					Thread.sleep(60000);
				} catch(InterruptedException err) {
					if(currentThread==this.thread) {
						LogFactory.getLogger(myServletContext, VncConsoleProxySocketServer.class).log(Level.WARNING, null, err);
					}
				}
			}
		}
	}
}
