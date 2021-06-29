/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2015, 2016, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts.signup;

import com.aoapps.net.URIEncoder;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.billing.PackageCategory;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.web.struts.SiteSettings;
import com.aoindustries.web.struts.Skin;
import com.aoindustries.web.struts.SkinAction;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author  AO Industries, Inc.
 */
public class IndexAction extends SkinAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SiteSettings siteSettings, Locale locale, Skin skin) throws Exception {
		AOServConnector rootConn = SiteSettings.getInstance(getServlet().getServletContext()).getRootAOServConnector();

		// Determine the active packages per category
		Map<PackageCategory, List<PackageDefinition>> categories = rootConn.getCurrentAdministrator().getUsername().getPackage().getAccount().getActivePackageDefinitions();
		// 404 when no packages defined
		if(categories.isEmpty()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		// 301 redirect when only one package category is available
		if(categories.size()==1) {
			String urlBase = skin.getUrlBase(request);
			String categoryName = categories.keySet().iterator().next().getName();
			if(PackageCategory.AOSERV.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/aoserv.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.APPLICATION.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/application.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.BACKUP.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/backup.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.COLOCATION.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/colocation.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.DEDICATED.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/dedicated-server.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.MANAGED.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/managed-server.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.RESELLER.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/reseller.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.SYSADMIN.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/system-administration.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.VIRTUAL.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/virtual-hosting.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.VIRTUAL_DEDICATED.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/virtual-dedicated-server.do"
						)
					)
				);
				return null;
			}
			if(PackageCategory.VIRTUAL_MANAGED.equals(categoryName)) {
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							urlBase + "signup/virtual-managed-server.do"
						)
					)
				);
				return null;
			}
			throw new ServletException("Unsupported package category: "+categoryName);
		}
		request.setAttribute("categories", categories);
		return mapping.findForward("success");
	}
}
