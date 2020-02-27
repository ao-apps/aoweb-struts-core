/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.util.WrappedExceptions;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;

/**
 * @author  AO Industries, Inc.
 */
public class ExceptionHandler extends org.apache.struts.action.ExceptionHandler {

	private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

	@Override
	public ActionForward execute(Exception exception, ExceptionConfig config, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ServletContext servletContext = request.getServletContext();

		// There are two sources for exceptions, not sure if these are the same because the original exception from a bean access in JSP is lost
		// 1) The exception passed in here
		// 2) Globals.EXCEPTION_KEY
		Throwable globalsException = (Throwable)request.getAttribute(Globals.EXCEPTION_KEY);
		if(exception!=null) {
			if(globalsException!=null) logger.log(Level.SEVERE, null, new WrappedExceptions(exception, globalsException));
			else logger.log(Level.SEVERE, null, exception);
		} else {
			if(globalsException!=null) logger.log(Level.SEVERE, null, globalsException);
			// Do nothing, neither exception exists
		}

		// Resolve the SiteSettings, to be compatible with SiteSettingsAction
		SiteSettings siteSettings;
		try {
			siteSettings = SiteSettings.getInstance(servletContext);
		} catch(Exception err) {
			logger.log(Level.SEVERE, null, err);
			// Use default settings
			siteSettings = new SiteSettings(servletContext);
		}
		request.setAttribute(Constants.SITE_SETTINGS, siteSettings);

		// Resolve the Locale, to be compatible with LocaleAction
		Locale locale;
		try {
			locale = LocaleAction.getEffectiveLocale(siteSettings, request, response);
		} catch(RuntimeException | IOException | SQLException | JspException err) {
			logger.log(Level.SEVERE, null, err);
			// Use default locale
			locale = Locale.getDefault();
		}
		request.setAttribute(Constants.LOCALE, locale);

		// Select Skin, to be compatible with SkinAction
		Skin skin;
		try {
			skin = SkinAction.getSkin(siteSettings, request, response);
		} catch(RuntimeException | JspException err) {
			logger.log(Level.SEVERE, null, err);
			// Use text skin
			skin = TextSkin.getInstance();
		}
		request.setAttribute(Constants.SKIN, skin);

		if(exception!=null && exception!=globalsException) request.setAttribute("exception", exception);

		return mapping.findForward("exception");
	}
}
