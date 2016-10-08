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

	public SessionRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public HttpSession getSession() {
		HttpSession session = super.getSession();
		if(session!=null) {
			if(!(session instanceof FilteredHttpSession)) session = new FilteredHttpSession(session);
		}
		return session;
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession session = super.getSession(create);
		if(session!=null) {
			if(!(session instanceof FilteredHttpSession)) session = new FilteredHttpSession(session);
		}
		return session;
	}
}
