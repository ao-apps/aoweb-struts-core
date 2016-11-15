/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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

import com.aoindustries.util.i18n.ModifiableResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author  AO Industries, Inc.
 */
public class SetResourceBundleValueAction extends SkinAction {

	@Override
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale userLocale,
		Skin skin
	) throws Exception {
		// If disabled, return 404 status
		if(!siteSettings.getCanEditResources()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		/*Enumeration names = request.getHeaderNames();
		while(names.hasMoreElements()) {
			String name = (String)names.nextElement();
			System.out.println(name);
			Enumeration values = request.getHeaders(name);
			while(values.hasMoreElements()) {
				System.out.println("    "+values.nextElement());
			}
		}*/
		String baseName = request.getParameter("baseName");
		Locale locale = new Locale(request.getParameter("locale")); // TODO: Parse country and variant, too.
		String key = request.getParameter("key");
		String value = request.getParameter("value");
		//for(int c=0;c<value.length();c++) System.out.println(Integer.toHexString(value.charAt(c)));
		boolean modified = "true".equals(request.getParameter("modified"));

		// Find the bundle
		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
		if(!resourceBundle.getLocale().equals(locale)) throw new AssertionError("resourceBundle.locale!=locale");
		if(!(resourceBundle instanceof ModifiableResourceBundle)) throw new AssertionError("resourceBundle is not a ModifiableResourceBundle");
		((ModifiableResourceBundle)resourceBundle).setString(key, value, modified);

		// Set request parameters
		request.setAttribute("baseName", baseName);
		request.setAttribute("locale", locale);
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		request.setAttribute("modified", modified);

		// Return success
		return mapping.findForward("success");
	}
}
