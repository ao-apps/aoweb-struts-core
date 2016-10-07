/*
 * Copyright 2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * Blocks direct (REQUEST) access to any .jsp, redirects /index.jsp to /index.do
 *
 * @author  AO Industries, Inc.
 */
public class NoDirectJspFilter implements Filter {

	private ServletContext servletContext;

	@Override
	public void init(FilterConfig config) {
		servletContext = config.getServletContext();
	}

	@Override
	public void doFilter(
		ServletRequest request,
		ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		String servletPath = httpRequest.getServletPath();
		if(servletPath.equals("/index.jsp")) {
			try {
				SiteSettings siteSettings = SiteSettings.getInstance(servletContext);
				// Locale locale = LocaleAction.getEffectiveLocale(siteSettings, httpRequest, httpResponse);
				Skin skin = SkinAction.getSkin(siteSettings, httpRequest, httpResponse);
				StringBuilder url = new StringBuilder(skin.getUrlBase(httpRequest)).append("index.do");
				String query = httpRequest.getQueryString();
				if(query!=null) url.append('?').append(query);
				httpResponse.setHeader("Location", url.toString());
				httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
			} catch(JspException err) {
				throw new ServletException(err);
			//} catch(SQLException err) {
			//    throw new ServletException(err);
			}
		} else {
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	public void destroy() {
		servletContext = null;
	}
}
