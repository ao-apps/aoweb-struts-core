/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

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
 * @author Dan Armstrong &lt;dan@aoindustries.com&gt;
 */
public class SkinAction extends LocaleAction {

	/**
	 * Gets the default skin from the provided list for the provided request.
	 * Blackberry and Lynx will default to "Text" if in the list, otherwise
	 * the first skin is selected.
	 */
	public static Skin getDefaultSkin(List<Skin> skins, HttpServletRequest req) {
		// Lynx and BlackBerry default to text
		String agent = req.getHeader("user-agent");
		if(
			agent!=null
			&& (
				agent.toLowerCase().indexOf("lynx") != -1
				|| agent.startsWith("BlackBerry")
			)
		) {
			for(Skin skin : skins) {
				if(skin.getName().equals("Text")) return skin;
			}
		}
		// Use the first as the default
		return skins.get(0);
	}

	/**
	 * Gets the skin for the current request.
	 *
	 * <ol>
	 *   <li>If the parameter "layout" exists, it will get the class name for the skin from the servlet parameters and set the skin.</li>
	 *   <li>If the parameter "layout" doesn't exist and a skin has been selected, then it returns the current skin.</li>
	 *   <li>Sets the skin from the servlet parameters for "Default".</li>
	 * </ol>
	 */
	public static Skin getSkin(SiteSettings settings, HttpServletRequest req, HttpServletResponse resp) throws JspException {
		List<Skin> skins = settings.getSkins();

		String layout = req.getParameter("layout");
		// Trim and set to null if empty
		if(layout!=null && (layout=layout.trim()).length()==0) layout=null;

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
	 * It also stores any "su" request for later processing by AuthenticatedAction.
	 *
	 * @see #execute(ActionMapping,ActionForm,HttpServletRequest,HttpServletResponse,Locale,Skin)
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

		// Is a "su" requested?
		String su=request.getParameter("su");
		if(su!=null) {
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
