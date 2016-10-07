/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Sets the request encoding based on the users locale stored in their session.  If
 * the session doesn't exist or doesn't have a locale set, will not set character
 * encoding, which leaves the application server default behavior.
 *
 * @author  AO Industries, Inc.
 */
public class SetRequestCharacterEncodingFilter implements Filter {

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
		HttpSession session = httpRequest.getSession(false);
		if(session!=null) {
			ServletContext servletContext = session.getServletContext();
			try {
				SiteSettings siteSettings = SiteSettings.getInstance(servletContext);
				HttpServletResponse httpResponse = (HttpServletResponse)response;
				Locale locale = SkinAction.getEffectiveLocale(siteSettings, httpRequest, httpResponse);
				if(locale!=null) {
					request.setCharacterEncoding(Skin.getCharacterSet(locale));
				}
			} catch(Exception err) {
				LogFactory.getLogger(servletContext, SetRequestCharacterEncodingFilter.class).log(Level.WARNING, null, err);
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
