/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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
package com.aoindustries.website;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prevents the <code>;jsessionid</code> from being added for SEO purposes.
 * If the client doesn't support cookies:
 * <ol>
 *   <li>If this site supports more than one language, adds a language parameter if it doesn't exist.</li>
 *   <li>If this site supports more than one skin, adds a layout parameter if it doesn't exist.</li>
 * </ol>
 *
 * @author  AO Industries, Inc.
 */
public class SessionFilter implements Filter {

	@Override
	public void init(FilterConfig config) {
	}

	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		SessionResponseWrapper myresponse = new SessionResponseWrapper(httpRequest, (HttpServletResponse)response);
		SessionRequestWrapper myrequest = new SessionRequestWrapper(httpRequest);
		chain.doFilter(myrequest, myresponse);
		// Could improve the efficiency by removing temporary sessions proactively here
		/*
		// The only time we keep the session data is when the user is logged-in or supports cookie-based sessions
		HttpSession session = myrequest.getSession(false);
		if(session!=null) {
			if(session.isNew()...
			try {
				session.invalidate();
			} catch(IllegalStateException err) {
				// Ignore this because the session could have been already invalidated
			}
		}*/
	}

	@Override
	public void destroy() {
	}
}
