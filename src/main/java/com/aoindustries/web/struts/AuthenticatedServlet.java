/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts;

import com.aoapps.net.URIEncoder;
import com.aoindustries.aoserv.client.AOServConnector;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Ensures the user is logged in.  Forwards to "login" if not logged in.  Next it checks the user permissions and returns status 403 if they don't have the
 * proper permissions.  Otherwise, it calls the subclass doGet.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AuthenticatedServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	final public void doGet(
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException {
		// Must be logged in
		HttpSession session = request.getSession(false);
		AOServConnector aoConn = (session == null) ? null : (AOServConnector)session.getAttribute(Constants.AO_CONN);
		if(aoConn == null) {
			// Save target for later
			String target = request.getRequestURL().toString();
			if(!target.endsWith("/login.do")) {
				String queryString = request.getQueryString();
				if(queryString!=null) target = target+'?'+queryString;
				if(session == null) session = request.getSession();
				session.setAttribute(Constants.AUTHENTICATION_TARGET, target);
			} else {
				if(session != null) {
					session.removeAttribute(Constants.AUTHENTICATION_TARGET);
				}
			}

			int port = request.getServerPort();
			String url;
			if(port!=80 && port!=443) {
				// Development area
				url = "https://"+request.getServerName()+":11257"+request.getContextPath()+"/login.do";
			} else {
				url = "https://"+request.getServerName()+request.getContextPath()+"/login.do";
			}
			response.sendRedirect(
				response.encodeRedirectURL(
					URIEncoder.encodeURI(
						url
					)
				)
			);
		} else {
			doGet(request, response, aoConn);
		}
	}

	abstract public void doGet(
		HttpServletRequest request,
		HttpServletResponse response,
		AOServConnector aoConn
	) throws IOException;
}
