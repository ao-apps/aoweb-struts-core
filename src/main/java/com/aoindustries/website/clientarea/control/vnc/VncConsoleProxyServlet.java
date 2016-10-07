/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.vnc;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Managed all VNC Proxies, including direct SSL sockets and HTTPS connections.
 *
 * @author  AO Industries, Inc.
 */
@WebServlet(
	urlPatterns = "/clientarea/control/vnc/vnc-console-proxy",
	loadOnStartup = 3
)
public class VncConsoleProxyServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private VncConsoleProxySocketServer socketServer;

	@Override
	public void init() {
		socketServer = new VncConsoleProxySocketServer();
		socketServer.init(getServletContext());
	}

	@Override
	public void destroy() {
		VncConsoleProxySocketServer mySocketServer = this.socketServer;
		if(mySocketServer!=null) {
			this.socketServer = null;
			mySocketServer.destroy();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Require SSL
		if(!req.isSecure()) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			// Implement here when needed
			resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Require SSL
		if(!req.isSecure()) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			// Implement here when needed
			resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}
	}
}
