/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.linux.User;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

/**
 * @author  AO Industries, Inc.
 */
public class LoginCompletedAction extends SkinAction {

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
		LoginForm loginForm = (LoginForm)form;

		ActionMessages errors = loginForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		User.Name username = User.Name.valueOf(loginForm.getUsername());
		String password = loginForm.getPassword();

		ServletContext servletContext = getServlet().getServletContext();
		try {
			// Get connector
			AOServConnector aoConn = AOServConnector.getConnector(username, password, LogFactory.getLogger(servletContext, LoginCompletedAction.class));
			aoConn.ping();

			// Store in session
			HttpSession session = request.getSession();
			session.setAttribute(Constants.AUTHENTICATED_AO_CONN, aoConn);
			session.setAttribute(Constants.AO_CONN, aoConn);

			// Try redirect
			String target = (String)session.getAttribute(Constants.AUTHENTICATION_TARGET);   // Get from session
			if(target==null) target = request.getParameter(Constants.AUTHENTICATION_TARGET); // With no cookies will be encoded in URL
			if(target!=null && target.length()>0) {
				response.sendRedirect(response.encodeRedirectURL(target));
				return null;
			}

			// Redirect to the clientarea instead of returning mapping because of switch from HTTPS to HTTP
			response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/index.do"));
			return null;
			// Return success
			//return mapping.findForward("success");
		} catch(IOException err) {
			String message=err.getMessage();
			if(message!=null) {
				MessageResources applicationResources = (MessageResources)request.getAttribute("/ApplicationResources");
				if(message.contains("Unable to find BusinessAdministrator")) message=applicationResources.getMessage(locale, "login.accountNotFound");
				else if(message.contains("Connection attempted with invalid password")) message=applicationResources.getMessage(locale, "login.badPassword");
				else if(message.contains("BusinessAdministrator disabled")) message=applicationResources.getMessage(locale, "accountDisabled");
				else message=null;
			}
			if(message!=null) request.setAttribute(Constants.AUTHENTICATION_MESSAGE, message);
			else LogFactory.getLogger(servletContext, LoginCompletedAction.class).log(Level.SEVERE, null, err);
			return mapping.findForward("failure");
		}
	}
}
