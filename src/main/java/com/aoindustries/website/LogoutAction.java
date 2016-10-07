/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author  AO Industries, Inc.
 */
public class LogoutAction extends SkinAction {

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
		// Handle logout
		HttpSession session = request.getSession();
		session.removeAttribute(Constants.AO_CONN);
		session.removeAttribute(Constants.AUTHENTICATED_AO_CONN);
		session.removeAttribute(Constants.AUTHENTICATION_TARGET);
		session.removeAttribute(Constants.SU_REQUESTED);

		// Try redirect
		String target = request.getParameter("target");
		if(target!=null && target.length()>0) {
			response.sendRedirect(response.encodeRedirectURL(target));
			return null;
		}

		return mapping.findForward("success");
	}
}
