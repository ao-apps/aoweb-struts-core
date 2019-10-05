/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2019  AO Industries, Inc.
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

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * @author  AO Industries, Inc.
 */
public class SessionTimeoutAction extends SkinAction {

	@Override
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin
	) throws Exception {
		// Logout, just in case session not actually expired
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.removeAttribute(Constants.AO_CONN);
			session.removeAttribute(Constants.AUTHENTICATED_AO_CONN);
			session.removeAttribute(Constants.AUTHENTICATION_TARGET);
			session.removeAttribute(Constants.SU_REQUESTED);
		}

		// Save the target so authentication will return to the previous page
		String target = request.getParameter("target");
		if(target!=null && target.length()>0 && !target.endsWith("/login.do")) {
			if(session == null) session = request.getSession();
			session.setAttribute(Constants.AUTHENTICATION_TARGET, target);
		}

		// Set the authenticationMessage
		MessageResources applicationResources = (MessageResources)request.getAttribute("/ApplicationResources");
		request.setAttribute(Constants.AUTHENTICATION_MESSAGE, applicationResources.getMessage(locale, "SessionTimeoutAction.authenticationMessage"));

		return mapping.findForward("success");
	}
}
