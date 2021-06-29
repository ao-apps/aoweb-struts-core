/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts.clientarea.control.vnc;

import com.aoapps.hodgepodge.io.AOPool;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.Throwables;
import com.aoapps.net.InetAddress;
import com.aoindustries.aoserv.client.AOServClientConfiguration;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.infrastructure.VirtualServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.daemon.client.AOServDaemonConnection;
import com.aoindustries.aoserv.daemon.client.AOServDaemonConnector;
import com.aoindustries.aoserv.daemon.client.AOServDaemonProtocol;
import com.aoindustries.web.struts.SiteSettings;
import static com.aoindustries.web.struts.clientarea.control.vnc.VncConsoleProxySocketHandler.desCipher;
import static com.aoindustries.web.struts.clientarea.control.vnc.VncConsoleProxySocketHandler.protocolVersion_3_3;
import static com.aoindustries.web.struts.clientarea.control.vnc.VncConsoleProxySocketHandler.protocolVersion_3_5;
import static com.aoindustries.web.struts.clientarea.control.vnc.VncConsoleProxySocketHandler.protocolVersion_3_8;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Handles websocket connections and proxies through to the
 * behind-the-scenes VNC server.
 *
 * @author  AO Industries, Inc.
 */
@ServerEndpoint(value = "/clientarea/control/vnc/vnc-console-websocket")
public class VncConsoleProxyWebsocketServer {

	private static final Logger logger = Logger.getLogger(VncConsoleProxyWebsocketServer.class.getName());

	@WebListener
	public static class Initializer implements ServletContextListener {

		private static ServletContext servletContext;

		@Override
		public void contextInitialized(ServletContextEvent event) {
			servletContext = event.getServletContext();
			logger.fine("Got servlet context");
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
		}
	}

	public VncConsoleProxyWebsocketServer() {
		logger.fine("Constructed");
	}

	private enum Phase {
		Protocol,
		Auth,
		Proxy,
		Error
	}

	private volatile Phase phase = Phase.Protocol;
	private volatile Future<Void> protocolFuture;
	private volatile Future<Void> authFuture;
	private volatile Future<Void> proxyFuture;
	private volatile AOServDaemonConnection daemonConn;
	private volatile StreamableInput daemonIn;
	private volatile StreamableOutput daemonOut;
	private volatile Thread outThread;

	@OnOpen
	public void onOpen(Session session) {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine(
				"session.id: " + session.getId()
				+ ", session.negotiatedSubprotocol: " + session.getNegotiatedSubprotocol()
				+ ", session.protocolVersion: " + session.getProtocolVersion()
			);
		}
		protocolFuture = session.getAsyncRemote().sendBinary(ByteBuffer.wrap(protocolVersion_3_3));
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) throws IOException {
		logger.fine("closing session.id: " + session.getId() + ", reason: " + reason);
		StreamableInput _daemonIn = daemonIn;
		if(_daemonIn != null) {
			_daemonIn.close();
		}
		StreamableOutput _daemonOut = daemonOut;
		if(_daemonOut != null) {
			_daemonOut.close();
		}
		AOServDaemonConnection _daemonConn = daemonConn;
		if(_daemonConn != null) {
			// Always close after VNC tunnel since this is a connection-terminal command
			daemonConn = null;
			_daemonConn.abort();
		}
		Thread _outThread = outThread;
		if(_outThread != null) {
			outThread = null;
			_outThread.interrupt();
		}
	}

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	@SuppressWarnings("VolatileArrayField")
	private volatile byte[] challenge;

	@OnMessage
	public void onMessage(byte[] message, Session session) throws IOException, SQLException, InterruptedException {
		if(phase == Phase.Protocol) {
			if(message != null) {
				buffer.write(message);
				message = null;
			}
			if(buffer.size() >= protocolVersion_3_3.length) {
				assert protocolFuture.isDone();
				byte[] bytes = buffer.toByteArray();
				buffer.reset();
				if(bytes.length > protocolVersion_3_3.length) {
					buffer.write(bytes, protocolVersion_3_3.length, bytes.length - protocolVersion_3_3.length);
				}
				for(int c=0; c<protocolVersion_3_3.length; c++) {
					int b = bytes[c];
					if(
						protocolVersion_3_3[c]!=b
						&& protocolVersion_3_5[c]!=b // Accept 3.5 but treat as 3.3
						&& protocolVersion_3_8[c]!=b // Accept 3.8 but treat as 3.3
					) throw new IOException("Mismatched protocolVersion from VNC client through socket: #"+c+": "+(char)b);
				}
				byte[] auth = new byte[4 + 16];
				// Security type
				auth[0] = 0;
				auth[1] = 0;
				auth[2] = 0;
				auth[3] = 2;
				// VNC Authentication
				assert challenge == null;
				challenge = new byte[16];
				AOServConnector.getSecureRandom().nextBytes(challenge);
				System.arraycopy(challenge, 0, auth, 4, 16);
				authFuture = session.getAsyncRemote().sendBinary(ByteBuffer.wrap(auth));
				phase = Phase.Auth;
			}
		}
		if(phase == Phase.Auth) {
			if(message != null) {
				buffer.write(message);
				message = null;
			}
			if(buffer.size() >= 16) {
				assert authFuture.isDone();
				byte[] bytes = buffer.toByteArray();
				buffer.reset();
				if(bytes.length > 16) {
					buffer.write(bytes, 16, bytes.length - 16);
				}
				byte[] response = new byte[16];
				System.arraycopy(bytes, 0, response, 0, 16);
				VirtualServer virtualServer = null;
				SiteSettings siteSettings = SiteSettings.getInstance(Initializer.servletContext);
				AOServConnector rootConn = siteSettings.getRootAOServConnector();
				for(VirtualServer vs : rootConn.getInfrastructure().getVirtualServer().getRows()) {
					String vncPassword = vs.getVncPassword();
					if(vncPassword!=null && !vncPassword.equals(AoservProtocol.FILTERED)) {
						byte[] expectedResponse = desCipher(challenge, vncPassword);
						if(Arrays.equals(response, expectedResponse)) {
							virtualServer = vs;
							break;
						}
					}
				}
				if(virtualServer==null) {
					// Virtual Host not found
					logger.warning("Virtual Host not found");
					Thread.sleep(5000);
					session.getAsyncRemote().sendBinary(ByteBuffer.wrap(new byte[] {0, 0, 0, 1}));
					session.close();
				} else {
					// Connect and authenticate to the real VNC server before sending security result

					// Connect through AOServ Platform
					Server.DaemonAccess daemonAccess = virtualServer.requestVncConsoleAccess();
					logger.fine("Got daemon access");
					AOServDaemonConnector daemonConnector = AOServDaemonConnector.getConnector(
						daemonAccess.getHost(),
						InetAddress.UNSPECIFIED_IPV4,
						daemonAccess.getPort(),
						daemonAccess.getProtocol(),
						null,
						100,
						AOPool.DEFAULT_MAX_CONNECTION_AGE,
						AOServClientConfiguration.getSslTruststorePath(),
						AOServClientConfiguration.getSslTruststorePassword()
					);
					logger.fine("Got daemon connector");
					AOServDaemonConnection _daemonConn = daemonConnector.getConnection();
					daemonConn = _daemonConn;
					logger.fine("Got daemon connection");
					daemonOut = _daemonConn.getRequestOut(AOServDaemonProtocol.VNC_CONSOLE);
					daemonOut.writeLong(daemonAccess.getKey());
					daemonOut.flush();
					logger.fine("Sent daemon request");

					daemonIn = _daemonConn.getResponseIn();
					int result=daemonIn.read();
					logger.fine("Got daemon result");
					if(result==AOServDaemonProtocol.NEXT) {
						// Authenticate to actual VNC
						// Protocol Version handshake
						for(int c=0; c<protocolVersion_3_3.length; c++) {
							int b = daemonIn.read();
							if(b == -1) throw new EOFException("EOF from daemonIn");
							if(
								protocolVersion_3_3[c]!=b // Hardware virtualized
								&& protocolVersion_3_8[c]!=b // Paravirtualized
							) throw new IOException("Mismatched protocolVersion from VNC server through daemon: #"+c+": "+(char)b);
						}
						daemonOut.write(protocolVersion_3_3);
						daemonOut.flush();
						// Security Type
						{
							int securityType1 = daemonIn.read();
							if(securityType1 == -1) throw new EOFException("EOF from daemonIn reading securityType1");
							int securityType2 = daemonIn.read();
							if(securityType2 == -1) throw new EOFException("EOF from daemonIn reading securityType2");
							int securityType3 = daemonIn.read();
							if(securityType3 == -1) throw new EOFException("EOF from daemonIn reading securityType3");
							int securityType4 = daemonIn.read();
							if(securityType4 == -1) throw new EOFException("EOF from daemonIn reading securityType4");
							if(
								securityType1 != 0
								|| securityType2 != 0
								|| securityType3 != 0
								|| securityType4 != 2
							) throw new IOException(
								"Mismatched security type from VNC server through daemon: ("
								+ securityType1
								+ ", " + securityType2
								+ ", " + securityType3
								+ ", " + securityType4
								+ ")"
							);
						}
						// VNC Authentication
						for(int c=0;c<16;c++) {
							int b = daemonIn.read();
							if(b == -1) throw new EOFException("EOF from daemonIn");
							challenge[c] = (byte)b;
						}
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
							session.getAsyncRemote().sendBinary(ByteBuffer.wrap(new byte[] {0, 0, 0, 1}));
							session.close();
							throw new IOException("Authentication to real VNC server failed");
						}
						proxyFuture = session.getAsyncRemote().sendBinary(ByteBuffer.wrap(new byte[] {0, 0, 0, 0}));
						// daemonIn -> socketOut in another thread
						assert outThread == null;
						@SuppressWarnings({"BroadCatchBlock", "AssignmentToCatchBlockParameter"})
						Thread _outThread = new Thread(
							() -> {
								try {
									try {
										byte[] buff = new byte[4096];
										int ret;
										while((ret = daemonIn.read(buff, 0, 4096)) != -1) {
											proxyFuture.get();
											proxyFuture = session.getAsyncRemote().sendBinary(ByteBuffer.wrap(Arrays.copyOf(buff, ret)));
										}
										// Always close after VNC tunnel since this is a connection-terminal command
										session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "EOF at daemonIn"));
									} finally {
										logger.fine("EOF at daemonIn, closing daemonConn");
										// Always close after VNC tunnel since this is a connection-terminal command
										_daemonConn.abort();
									}
								} catch(ThreadDeath td) {
									throw td;
								} catch(Throwable t) {
									try {
										session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, t.toString()));
									} catch(Error | RuntimeException | IOException e) {
										t = Throwables.addSuppressed(t, e);
									}
									logger.log(Level.SEVERE, null, t);
								}
							},
							"VncConsoleProxyWebsocketServer daemonIn->socketOut: " + virtualServer.getHost().getName()
						);
						_outThread.setDaemon(true); // Don't prevent JVM shutdown
						_outThread.setPriority(Thread.NORM_PRIORITY+2); // Higher priority for higher performance
						outThread = _outThread;
						_outThread.start();
						phase = Phase.Proxy;
					} else {
						String errMessage = null;
						try {
							if (result == AOServDaemonProtocol.IO_EXCEPTION) throw new IOException(errMessage = daemonIn.readUTF());
							else if (result == AOServDaemonProtocol.SQL_EXCEPTION) throw new SQLException(errMessage = daemonIn.readUTF());
							else if (result == -1) throw new EOFException(errMessage = "EOF from daemonIn");
							else throw new IOException(errMessage = "Unknown result: " + result);
						} finally {
							session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, errMessage));
						}
					}
				}
			}
		}
		if(phase == Phase.Proxy) {
			// socketIn -> daemonOut in this thread
			boolean doFlush = false;
			if(buffer.size() > 0) {
				buffer.writeTo(daemonOut);
				buffer.reset();
				doFlush = true;
			}
			if(message != null && message.length > 0) {
				daemonOut.write(message);
				doFlush = true;
			}
			if(doFlush) {
				daemonOut.flush();
			}
		}
	}

	@OnError
	public void onError(Session session, Throwable t) throws IOException {
		phase = Phase.Error;
		try {
			logger.log(Level.SEVERE, null, t);
		} finally {
			// TODO: Required?  Helpful?
			session.close();
		}
	}
}
