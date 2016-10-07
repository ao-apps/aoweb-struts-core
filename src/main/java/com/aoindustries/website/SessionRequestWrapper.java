/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * @author  AO Industries, Inc.
 */
public class SessionRequestWrapper extends HttpServletRequestWrapper {

	final private SessionResponseWrapper response;

	public SessionRequestWrapper(HttpServletRequest request, SessionResponseWrapper response) {
		super(request);
		this.response = response;
	}

	@Override
	public HttpSession getSession() {
		HttpSession session = super.getSession();
		if(session!=null) {
			// Now all over HTTPS: processSessionCookie(session);
			if(!(session instanceof FilteredHttpSession)) session = new FilteredHttpSession(session);
		}
		return session;
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession session = super.getSession(create);
		if(session!=null) {
			// Now all over HTTPS: processSessionCookie(session);
			if(!(session instanceof FilteredHttpSession)) session = new FilteredHttpSession(session);
		}
		return session;
	}

	/**
	 * Now all over HTTPS: 
	private void processSessionCookie(HttpSession session) {
		if (null == response) {
			// No response object attached, skip the pre processing
			return;
		}
		// cookieOverWritten - Flag to filter multiple "Set-Cookie" headers
		Object cookieOverWritten = getAttribute("COOKIE_OVERWRITTEN_FLAG");
		//System.err.println("DEBUG: SessionSharingRequestWrapper: cookieOverWritten="+cookieOverWritten);
		//System.err.println("DEBUG: SessionSharingRequestWrapper: isSecure()="+isSecure());
		//System.err.println("DEBUG: SessionSharingRequestWrapper: isRequestedSessionIdFromCookie()="+isRequestedSessionIdFromCookie());
		//System.err.println("DEBUG: SessionSharingRequestWrapper: session.isNew()="+session.isNew());
		if (null == cookieOverWritten && isSecure() / *&& isRequestedSessionIdFromCookie()* / && session.isNew()) {
			// Might have created the cookie in SSL protocol and tomcat will lose the session
			// if there is change in protocol from HTTPS to HTTP. To avoid this, trick the browser
			// using the HTTP and HTTPS session cookie.
			Cookie cookie = new Cookie("JSESSIONID", session.getId());
			cookie.setMaxAge(-1); // Life of the browser or timeout
			String contextPath = getContextPath();
			if ((contextPath != null) && (contextPath.length() > 0)) {
				cookie.setPath(contextPath);
			} else {
				cookie.setPath("/");
			}
			//System.err.println("DEBUG: SessionSharingRequestWrapper: Setting cookie:");
			//System.err.println("    cookie.getComment()="+cookie.getComment());
			//System.err.println("    cookie.getDomain()="+cookie.getDomain());
			//System.err.println("    cookie.getMaxAge()="+cookie.getMaxAge());
			//System.err.println("    cookie.getName()="+cookie.getName());
			//System.err.println("    cookie.getPath()="+cookie.getPath());
			//System.err.println("    cookie.getSecure()="+cookie.getSecure());
			//System.err.println("    cookie.getValue()="+cookie.getValue());
			//System.err.println("    cookie.getVersion()="+cookie.getVersion());

			response.addCookie(cookie); // Adding an "Set-Cookie" header to the response
			setAttribute("COOKIE_OVERWRITTEN_FLAG", "true");// To avoid multiple "Set-Cookie" header
		}
	}
	 */
}
