/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009-2013, 2015, 2016, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.servlet.http.Cookies;
import com.aoindustries.util.i18n.EditableResourceBundle;
import com.aoindustries.website.struts.ResourceBundleMessageResources;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Resolves the current SiteSettings, sets the request param siteSettings, and calls subclass implementation.
 *
 * @author AO Industries, Inc.
 */
// TODO: Convert to ServletRequestListener
public class SiteSettingsAction extends Action {

	/**
	 * Resolves the <code>SiteSettings</code>, sets the request attribute "siteSettings", then the subclass execute method is invoked.
	 *
	 * @see #execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.website.SiteSettings)
	 */
	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response
	) throws Exception {
		// Resolve the settings
		SiteSettings siteSettings = SiteSettings.getInstance(getServlet().getServletContext());
		request.setAttribute(Constants.SITE_SETTINGS, siteSettings);

		// Start the request tracking
		boolean canEditResources = siteSettings.getCanEditResources();
		boolean modifyAllText = false;
		if(canEditResources) {
			// Check for cookie
			if("visible".equals(Cookies.getCookie(request, "EditableResourceBundleEditorVisibility"))) {
				modifyAllText = true;
			}
		}
		ResourceBundleMessageResources.setCachedEnabled(!canEditResources);
		EditableResourceBundle.resetRequest(
			canEditResources,
			canEditResources ? (Skin.getDefaultUrlBase(request)+"set-resource-bundle-value.do") : null,
			modifyAllText
		);

		return execute(mapping, form, request, response, siteSettings);
	}

	/**
	 * Once the siteSettings are resolved, this version of the execute method is invoked.
	 * The default implementation of this method simply returns the mapping of "success".
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings
	) throws Exception {
		return mapping.findForward("success");
	}
}
