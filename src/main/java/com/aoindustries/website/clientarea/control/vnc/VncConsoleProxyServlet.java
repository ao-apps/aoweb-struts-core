/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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
