/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
 * Shared the sessionid cookie between HTTP and HTTPS servers.
 * Also prevents the <code>;jsessionid</code> from being added for SEO purposes.
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
		SessionRequestWrapper myrequest = new SessionRequestWrapper(httpRequest, myresponse);
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
