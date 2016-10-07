/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.util.WrappedExceptions;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;

/**
 * @author  AO Industries, Inc.
 */
public class ExceptionHandler extends org.apache.struts.action.ExceptionHandler {

	@Override
	public ActionForward execute(Exception exception, ExceptionConfig config, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ServletContext servletContext = request.getSession().getServletContext();
		Logger logger = LogFactory.getLogger(servletContext, ExceptionHandler.class);

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
		} catch(Exception err) {
			logger.log(Level.SEVERE, null, err);
			// Use default locale
			locale = Locale.getDefault();
		}
		request.setAttribute(Constants.LOCALE, locale);

		// Select Skin, to be compatible with SkinAction
		Skin skin;
		try {
			skin = SkinAction.getSkin(siteSettings, request, response);
		} catch(Exception err) {
			logger.log(Level.SEVERE, null, err);
			// Use text skin
			skin = TextSkin.getInstance();
		}
		request.setAttribute(Constants.SKIN, skin);

		if(exception!=null && exception!=globalsException) request.setAttribute("exception", exception);

		return mapping.findForward("exception");
	}
}
