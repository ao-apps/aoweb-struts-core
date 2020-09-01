/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2020  AO Industries, Inc.
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
	public static <T extends ActionForm> T getSessionActionForm(ActionServlet servlet, HttpSession session, Class<T> clazz, String name) throws ReflectiveOperationException {
		Object existing = session.getAttribute(name);
		if(existing!=null) return clazz.cast(existing);
		T form = clazz.getConstructor().newInstance();
		form.setServlet(servlet);
		session.setAttribute(name, form);
		return form;
	}
}
