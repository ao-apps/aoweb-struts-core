/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionServlet;

/**
 * Utilities usable by any signup step.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupHelper {

	/**
	 * Make no instances.
	 */
	private SignupHelper() {}

	/**
	 * Gets the form of the provided class from the session.  If it is not in
	 * the session will create the form, set its servlet, and add it to the
	 * session.
	 */
	public static <T extends ActionForm> T getSessionActionForm(ActionServlet servlet, HttpSession session, Class<T> clazz, String name) throws InstantiationException, IllegalAccessException {
		Object existing = session.getAttribute(name);
		if(existing!=null) return clazz.cast(existing);
		T form = clazz.newInstance();
		form.setServlet(servlet);
		session.setAttribute(name, form);
		return form;
	}
}
