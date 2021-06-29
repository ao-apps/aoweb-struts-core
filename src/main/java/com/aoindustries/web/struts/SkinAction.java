/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts;

import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Resolves the current skin, sets the request param skin, and calls subclass implementation.
 *
 * @author AO Industries, Inc.
 */
// TODO: Convert to ServletRequestListener, like done in affiliates project
public class SkinAction extends LocaleAction {

	/**
	 * Gets the default skin from the provided list for the provided request.
	 * Blackberry and Lynx will default to {@link TextSkin#NAME} if in the list, otherwise
	 * the first skin is selected.
	 */
	public static Skin getDefaultSkin(List<Skin> skins, HttpServletRequest req) {
		// Lynx and BlackBerry default to text
		String agent = req.getHeader("user-agent");
		if(
			agent!=null
			&& (
				agent.toLowerCase().contains("lynx")
				|| agent.startsWith("BlackBerry")
			)
		) {
			for(Skin skin : skins) {
				if(skin.getName().equals(TextSkin.NAME)) return skin;
			}
		}
		// Use the first as the default
		return skins.get(0);
	}

	/**
	 * Gets the skin for the current request.
	 *
	 * <ol>
	 *   <li>If the parameter {@link Constants#LAYOUT} exists, it will get the class name for the skin from the servlet parameters and set the skin.</li>
	 *   <li>If the parameter {@link Constants#LAYOUT} doesn't exist and a skin has been selected, then it returns the current skin.</li>
	 *   <li>Sets the skin from the servlet parameters.</li>
	 * </ol>
	 */
	public static Skin getSkin(SiteSettings settings, HttpServletRequest req, HttpServletResponse resp) throws JspException {
		List<Skin> skins = settings.getSkins();

		String layout = req.getParameter(Constants.LAYOUT);
		// Trim and set to null if empty
		if(layout!=null && (layout=layout.trim()).length()==0) layout=null;

		// TODO: Avoid creating session and don't store in session for default layout?
		HttpSession session = req.getSession();

		if(layout!=null) {
			// Match against possibilities
			for(Skin skin : skins) {
				if(skin.getName().equals(layout)) {
					session.setAttribute(Constants.LAYOUT, layout);
					return skin;
				}
			}
		}

		// Try to reuse the currently selected skin
		layout = (String)session.getAttribute(Constants.LAYOUT);
		if(layout!=null) {
			// Match against possibilities
			for(Skin skin : skins) {
				if(skin.getName().equals(layout)) {
					session.setAttribute(Constants.LAYOUT, layout);
					return skin;
				}
			}
		}
		Skin skin = getDefaultSkin(skins, req);
		session.setAttribute(Constants.LAYOUT, skin.getName());
		return skin;
	}

	/**
	 * Selects the <code>Skin</code>, sets the request attribute "skin", then the subclass execute method is invoked.
	 * It also stores any {@link Constants#SU} request for later processing by AuthenticatedAction.
	 *
	 * @see #execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.web.struts.SiteSettings, java.util.Locale, com.aoindustries.web.struts.Skin)
	 */
	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale
	) throws Exception {
		// Select Skin
		Skin skin = getSkin(siteSettings, request, response);
		request.setAttribute(Constants.SKIN, skin);

		// Is a switch-user requested?
		String su = request.getParameter(Constants.SU);
		if(su != null) {
			request.getSession().setAttribute(Constants.SU_REQUESTED, su.trim());
		}

		return execute(mapping, form, request, response, siteSettings, locale, skin);
	}

	/**
	 * Once the skin is selected, this version of the execute method is invoked.
	 * The default implementation of this method simply returns the mapping of "success".
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin
	) throws Exception {
		return mapping.findForward("success");
	}
}
