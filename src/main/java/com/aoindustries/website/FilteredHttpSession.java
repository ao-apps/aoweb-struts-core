/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import static com.aoindustries.website.ApplicationResources.accessor;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.struts.Globals;

/**
 * Filters setAttribute to make sure all session objects are precisely as
 * expected by <code>SessionResponseWrapper</code> to ensure proper conditional
 * use of <code>jsessionid</code>
 *
 * @author  AO Industries, Inc.
 */
public class FilteredHttpSession implements HttpSession {

	private final HttpSession wrapped;

	public FilteredHttpSession(HttpSession wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public long getCreationTime() {
		return wrapped.getCreationTime();
	}

	@Override
	public String getId() {
		return wrapped.getId();
	}

	@Override
	public long getLastAccessedTime() {
		return wrapped.getLastAccessedTime();
	}

	@Override
	public ServletContext getServletContext() {
		return wrapped.getServletContext();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		wrapped.setMaxInactiveInterval(interval);
	}

	@Override
	public int getMaxInactiveInterval() {
		return wrapped.getMaxInactiveInterval();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		return wrapped.getSessionContext();
	}

	@Override
	public Object getAttribute(String name) {
		return wrapped.getAttribute(name);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public Object getValue(String name) {
		return wrapped.getValue(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return wrapped.getAttributeNames();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public String[] getValueNames() {
		return wrapped.getValueNames();
	}

	/**
	 * Makes sure the session attribute is acceptable.  Throws AssertionError
	 * if it is not.
	 */
	static void checkSessionAttribute(String name, Object value) {
		// All all null to be set
		if(value!=null) {
			// These names are always allowed
			if(
				!Constants.AUTHENTICATION_TARGET.equals(name)
				&& !Globals.LOCALE_KEY.equals(name)
				&& !Constants.LAYOUT.equals(name)
				&& !Constants.SU_REQUESTED.equals(name)
				&& !Constants.AO_CONN.equals(name)
				&& !Constants.AUTHENTICATED_AO_CONN.equals(name)
				// JSTL 1.1
				&& !"javax.servlet.jsp.jstl.fmt.request.charset".equals(name)
				&& !"javax.servlet.jsp.jstl.fmt.locale.session".equals(name)
				// Must be an SessionActionForm if none of the above
				&& !(value instanceof SessionActionForm)
			) {
				throw new AssertionError(accessor.getMessage("FilteredHttpSession.unexpectedSessionAttribute", name, value.getClass().getName()));
			}
		}
	}

	@Override
	public void setAttribute(String name, Object value) {
		checkSessionAttribute(name, value);
		wrapped.setAttribute(name, value);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void putValue(String name, Object value) {
		checkSessionAttribute(name, value);
		wrapped.putValue(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		wrapped.removeAttribute(name);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void removeValue(String name) {
		wrapped.removeValue(name);
	}

	@Override
	public void invalidate() {
		wrapped.invalidate();
	}

	@Override
	public boolean isNew() {
		return wrapped.isNew();
	}
}
