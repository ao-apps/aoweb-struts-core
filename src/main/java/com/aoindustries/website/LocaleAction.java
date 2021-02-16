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
package com.aoindustries.website;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.Config;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Resolves the current locale, optionally changing it with any language parameters, sets the request param locale, and calls subclass implementation.
 *
 * @author AO Industries, Inc.
 */
// TODO: Is it possible to convert to ServletRequestListener?
public class LocaleAction extends SiteSettingsAction {

	private static final Logger logger = Logger.getLogger(LocaleAction.class.getName());

	/**
	 * Gets the selected locale or the default locale if none has been selected.
	 */
	public static Locale getLocale(ServletContext servletContext, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			if(locale != null) return locale;
		}
		if(servletContext != null) {
			try {
				return getDefaultLocale(SiteSettings.getInstance(servletContext), request);
			} catch(IOException | SQLException | JspException err) {
				logger.log(Level.SEVERE, "Using default local", err);
			}
		}
		return Locale.getDefault();
	}

	/**
	 * Selects the <code>Locale</code>, sets the request attribute "locale", then the subclass execute method is invoked.
	 *
	 * @see #execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.website.SiteSettings, java.util.Locale)
	 */
	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings
	) throws Exception {
		// Resolve the locale
		Locale locale = getEffectiveLocale(siteSettings, request, response);
		request.setAttribute(Constants.LOCALE, locale);

		return execute(mapping, form, request, response, siteSettings, locale);
	}

	/**
	 * <p>
	 * Gets the effective locale for the request.  If the requested language is not
	 * one of the enabled languages for this site, will set to the default language
	 * (the first in the language list).
	 * </p>
	 * <p>Also allows the parameter {@link Constants#LANGUAGE} to override the current settings.</p>
	 * <p>This also sets the struts, JSTL, and response locales to the same value.</p>
	 */
	public static Locale getEffectiveLocale(SiteSettings siteSettings, HttpServletRequest request, HttpServletResponse response) throws JspException, IOException, SQLException {
		HttpSession session = request.getSession();
		List<Skin.Language> languages = siteSettings.getLanguages(request);
		Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
		String language = request.getParameter(Constants.LANGUAGE);
		if(language!=null && (language=language.trim()).length()>0) {
			// Make sure is a supported language
			for(Skin.Language possLanguage : languages) {
				String code = possLanguage.getCode();
				if(code.equals(language)) {
					locale = locale==null ? new Locale(code) : new Locale(code, locale.getCountry(), locale.getVariant());
					session.setAttribute(Globals.LOCALE_KEY, locale);
					Config.set(session, Config.FMT_LOCALE, locale);
					response.setLocale(locale);
					return locale;
				}
			}
		}
		if(locale!=null) {
			// Make sure the language is a supported value, otherwise return the default language
			String localeLanguage = locale.getLanguage();
			for(Skin.Language possLanguage : languages) {
				if(possLanguage.getCode().equals(localeLanguage)) {
					// Current value is from session and is OK
					response.setLocale(locale);
					// Make sure the JSTL value matches
					if(!locale.equals(Config.get(session, Config.FMT_LOCALE))) Config.set(session, Config.FMT_LOCALE, locale);
					return locale;
				}
			}
		}
		// Return the default
		locale = getDefaultLocale(languages);
		session.setAttribute(Globals.LOCALE_KEY, locale);
		Config.set(session, Config.FMT_LOCALE, locale);
		response.setLocale(locale);
		return locale;
	}

	/**
	 * Gets the default locale for the provided request.  The session is not
	 * set.
	 */
	public static Locale getDefaultLocale(SiteSettings siteSettings, HttpServletRequest request) throws JspException, IOException, SQLException {
		return getDefaultLocale(siteSettings.getLanguages(request));
	}

	private static Locale getDefaultLocale(List<Skin.Language> languages) {
		return new Locale(languages.get(0).getCode());
	}

	/**
	 * Once the locale is selected, this version of the execute method is invoked.
	 * The default implementation of this method simply returns the mapping of "success".
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale
	) throws Exception {
		return mapping.findForward("success");
	}
}
