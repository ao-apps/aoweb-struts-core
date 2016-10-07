/*
 * Copyright 2009-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.vnc;

import com.aoindustries.aoserv.client.AOServClientConfiguration;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServProtocol;
import com.aoindustries.aoserv.client.AOServer;
import com.aoindustries.aoserv.client.VirtualServer;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.daemon.client.AOServDaemonConnection;
import com.aoindustries.aoserv.daemon.client.AOServDaemonConnector;
import com.aoindustries.aoserv.daemon.client.AOServDaemonProtocol;
import com.aoindustries.io.AOPool;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.website.LogFactory;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import javax.net.ssl.SSLHandshakeException;
import javax.servlet.ServletContext;

/**
 * Handles a single connection.
 *
 * @author  AO Industries, Inc.
 */
public class VncConsoleProxySocketHandler {

	private static final byte[] protocolVersion_3_3 = {
		// "RFB 003.003\n"
		(byte)'R',
		(byte)'F',
		(byte)'B',
		(byte)' ',
		(byte)'0',
		(byte)'0',
		(byte)'3',
		(byte)'.',
		(byte)'0',
		(byte)'0',
		(byte)'3',
		(byte)'\n'
	};
	private static final byte[] protocolVersion_3_5 = {
		// "RFB 003.005\n"
		(byte)'R',
		(byte)'F',
		(byte)'B',
		(byte)' ',
		(byte)'0',
		(byte)'0',
		(byte)'3',
		(byte)'.',
		(byte)'0',
		(byte)'0',
		(byte)'5',
		(byte)'\n'
	};
	private static final byte[] protocolVersion_3_8 = {
		// "RFB 003.008\n"
		(byte)'R',
		(byte)'F',
		(byte)'B',
		(byte)' ',
		(byte)'0',
		(byte)'0',
		(byte)'3',
		(byte)'.',
		(byte)'0',
		(byte)'0',
		(byte)'8',
		(byte)'\n'
	};
	public VncConsoleProxySocketHandler(final ServletContext servletContext, final AOServConnector rootConn, final Socket socket) {
		// This thread will read from socket
		Thread thread = new Thread(
			new Runnable() {
				@Override
				public void run() {
					try {
						final OutputStream socketOut = socket.getOutputStream();
						final InputStream socketIn = socket.getInputStream();

						// Find which server is being connected to based on the authentication information
						// Protocol Version handshake
						socketOut.write(protocolVersion_3_3);
						socketOut.flush();
						for(int c=0; c<protocolVersion_3_3.length; c++) {
							int b = socketIn.read();
							if(b==-1) throw new EOFException("EOF from socketIn");
							if(
								protocolVersion_3_3[c]!=b
								&& protocolVersion_3_5[c]!=b // Accept 3.5 but treat as 3.3
								&& protocolVersion_3_8[c]!=b // Accept 3.8 but treat as 3.3
							) throw new IOException("Mismatched protocolVersion from VNC client through socket: #"+c+": "+(char)b);
						}
						// Security type
						socketOut.write(0);
						socketOut.write(0);
						socketOut.write(0);
						socketOut.write(2);
						// VNC Authentication
						byte[] challenge = new byte[16];
						AOServConnector.getRandom().nextBytes(challenge);
						socketOut.write(challenge);
						socketOut.flush();
						byte[] response = new byte[16];
						for(int c=0;c<16;c++) if((response[c] = (byte)socketIn.read())==-1) throw new EOFException("EOF from socketIn");
						VirtualServer virtualServer = null;
						for(VirtualServer vs : rootConn.getVirtualServers().getRows()) {
							String vncPassword = vs.getVncPassword();
							if(vncPassword!=null && !vncPassword.equals(AOServProtocol.FILTERED)) {
								byte[] expectedResponse = desCipher(challenge, vncPassword);
								if(Arrays.equals(response, expectedResponse)) {
									virtualServer = vs;
									break;
								}
							}
						}
						if(virtualServer==null) {
							// Virtual Server not found
							Thread.sleep(5000);
							socketOut.write(0);
							socketOut.write(0);
							socketOut.write(0);
							socketOut.write(1);
							socketOut.flush();
						} else {
							// Connect and authenticate to the real VNC server before sending security result

							// Connect through AOServ system
							AOServer.DaemonAccess daemonAccess = virtualServer.requestVncConsoleAccess();
							AOServDaemonConnector daemonConnector=AOServDaemonConnector.getConnector(
								daemonAccess.getHost(),
								InetAddress.UNSPECIFIED,
								daemonAccess.getPort(),
								daemonAccess.getProtocol(),
								null,
								100,
								AOPool.DEFAULT_MAX_CONNECTION_AGE,
								AOServClientConfiguration.getSslTruststorePath(),
								AOServClientConfiguration.getSslTruststorePassword(),
								LogFactory.getLogger(servletContext, getClass())
							);
							final AOServDaemonConnection daemonConn=daemonConnector.getConnection();
							try {
								final CompressedDataOutputStream daemonOut = daemonConn.getOutputStream();
								daemonOut.writeCompressedInt(AOServDaemonProtocol.VNC_CONSOLE);
								daemonOut.writeLong(daemonAccess.getKey());
								daemonOut.flush();

								final CompressedDataInputStream daemonIn = daemonConn.getInputStream();
								int result=daemonIn.read();
								if(result==AOServDaemonProtocol.NEXT) {
									// Authenticate to actual VNC
									// Protocol Version handshake
									for(int c=0; c<protocolVersion_3_3.length; c++) {
										int b = daemonIn.read();
										if(b==-1) throw new EOFException("EOF from daemonIn");
										if(
											protocolVersion_3_3[c]!=b // Hardware virtualized
											&& protocolVersion_3_8[c]!=b // Paravirtualized
										) throw new IOException("Mismatched protocolVersion from VNC server through daemon: #"+c+": "+(char)b);
									}
									daemonOut.write(protocolVersion_3_3);
									daemonOut.flush();
									// Security Type
									if(
										daemonIn.read()!=0
										|| daemonIn.read()!=0
										|| daemonIn.read()!=0
										|| daemonIn.read()!=2
									) throw new IOException("Mismatched security type from VNC server through daemon");
									// VNC Authentication
									for(int c=0;c<16;c++) if((challenge[c] = (byte)daemonIn.read())==-1) throw new EOFException("EOF from daemonIn");
									response = desCipher(challenge, virtualServer.getVncPassword());
									daemonOut.write(response);
									daemonOut.flush();
									if(
										daemonIn.read()!=0
										|| daemonIn.read()!=0
										|| daemonIn.read()!=0
										|| daemonIn.read()!=0
									) {
										Thread.sleep(5000);
										socketOut.write(0);
										socketOut.write(0);
										socketOut.write(0);
										socketOut.write(1);
										socketOut.flush();
										throw new IOException("Authentication to real VNC server failed");
									}
									socketOut.write(0);
									socketOut.write(0);
									socketOut.write(0);
									socketOut.write(0);

									// socketIn -> daemonOut in another thread
									Thread inThread = new Thread(
										new Runnable() {
											@Override
											public void run() {
												try {
													try {
														byte[] buff = new byte[4096];
														int ret;
														while((ret=socketIn.read(buff, 0, 4096))!=-1) {
															daemonOut.write(buff, 0, ret);
															daemonOut.flush();
														}
													} finally {
														daemonConn.close();
													}
												} catch(ThreadDeath TD) {
													throw TD;
												} catch(Throwable T) {
													LogFactory.getLogger(servletContext, getClass()).log(Level.SEVERE, null, T);
												}
											}
										},
										"VncConsoleProxySocketHandler socketIn->daemonOut"
									);
									inThread.setDaemon(true); // Don't prevent JVM shutdown
									inThread.setPriority(Thread.NORM_PRIORITY+2); // Higher priority for higher performance
									inThread.start();

									// daemonIn -> socketOut in this thread
									byte[] buff = new byte[4096];
									int ret;
									while((ret=daemonIn.read(buff, 0, 4096))!=-1) {
										socketOut.write(buff, 0, ret);
										socketOut.flush();
									}
								} else {
									if (result == AOServDaemonProtocol.IO_EXCEPTION) throw new IOException(daemonIn.readUTF());
									else if (result == AOServDaemonProtocol.SQL_EXCEPTION) throw new SQLException(daemonIn.readUTF());
									else if (result==-1) throw new EOFException("EOF from daemonIn");
									else throw new IOException("Unknown result: " + result);
								}
							} finally {
								daemonConn.close(); // Always close after VNC tunnel
								daemonConnector.releaseConnection(daemonConn);
							}
						}
					} catch(ThreadDeath TD) {
						throw TD;
					} catch(SocketException err) {
						// Do not log any socket exceptions
					} catch(SSLHandshakeException err) {
						String message = err.getMessage();
						if(!"Remote host closed connection during handshake".equals(message)) {
							LogFactory.getLogger(servletContext, getClass()).log(Level.INFO, null, err);
						}
					} catch(Throwable T) {
						LogFactory.getLogger(servletContext, getClass()).log(Level.SEVERE, null, T);
					} finally {
						try {
							socket.close();
						} catch(IOException err) {
							LogFactory.getLogger(servletContext, getClass()).log(Level.INFO, null, err);
						}
					}
				}
			},
			"VncConsoleProxySocketHandler daemonIn->socketOut"
		);
		thread.setDaemon(true); // Don't prevent JVM shutdown
		thread.setPriority(Thread.NORM_PRIORITY+2); // Higher priority for higher performance
		thread.start();
	}

	/**
	 * This is the same as AuthPanel.java
	 */
	public static byte[] desCipher(byte[] challenge, String password) {
		if(password.length() > 8) password = password.substring(0, 8);	// Truncate to 8 chars
		// vncEncryptBytes in the UNIX libvncauth truncates password
		// after the first zero byte. We do to.
		int firstZero = password.indexOf(0);
		if (firstZero != -1) password = password.substring(0, firstZero);
		byte[] key = {0, 0, 0, 0, 0, 0, 0, 0};
		System.arraycopy(password.getBytes(), 0, key, 0, password.length());
		byte[] response = new byte[16];
		System.arraycopy(challenge, 0, response, 0, 16);
		DesCipher des = new DesCipher(key);
		des.encrypt(response, 0, response, 0);
		des.encrypt(response, 8, response, 8);
		return response;
	}
}
