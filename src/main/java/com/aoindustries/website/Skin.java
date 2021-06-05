/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2013, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.encoding.Doctype;
import com.aoapps.encoding.Serialization;
import com.aoapps.encoding.servlet.DoctypeEE;
import com.aoapps.encoding.servlet.SerializationEE;
import com.aoapps.html.LINK;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.net.AnyURI;
import com.aoapps.net.URIEncoder;
import com.aoapps.servlet.http.HttpServletUtil;
import com.aoapps.web.resources.registry.Registry;
import com.aoindustries.website.skintags.PageAttributes;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.apache.struts.util.MessageResources;

/**
 * One look-and-feel for the website.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Throw IOException on most/all methods where is given a writer, then won't need so many exception conversions in implementations.
abstract public class Skin {

	/**
	 * Directional references.
	 */
	public static final int
		NONE=0,
		UP=1,
		DOWN=2,
		UP_AND_DOWN=3
	;

	/**
	 * Prints the links to the alternate translations of this page.
	 *
	 * <a href="https://support.google.com/webmasters/answer/189077?hl=en">https://support.google.com/webmasters/answer/189077?hl=en</a>
	 */
	public static void printAlternativeLinks(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, String fullPath, List<Language> languages) throws IOException {
		if(languages.size()>1) {
			// Default language
			{
				Language language = languages.get(0);
				AnyURI uri = language.getUri();
				// TODO: hreflang attribute
				document.link(LINK.Rel.ALTERNATE).hreflang("x-default").href(
					resp.encodeURL(
						URIEncoder.encodeURI(
							(
								uri == null
								? new AnyURI(fullPath).addEncodedParameter(Constants.LANGUAGE, URIEncoder.encodeURIComponent(language.getCode()))
								: uri
							).toASCIIString()
						)
					)
				).__();
			}
			// All languages
			for(Language language : languages) {
				AnyURI uri = language.getUri();
				document.link(LINK.Rel.ALTERNATE).hreflang(language.getCode()).href(
					resp.encodeURL(
						URIEncoder.encodeURI(
							(
								uri == null
								? new AnyURI(fullPath).addEncodedParameter(Constants.LANGUAGE, URIEncoder.encodeURIComponent(language.getCode()))
								: uri
							).toASCIIString()
						)
					)
				).__();
			}
		}
	}

	/**
	 * Gets the name of this skin.
	 */
	abstract public String getName();

	/**
	 * Gets the display value for this skin in the provided locale.
	 */
	abstract public String getDisplay(HttpServletRequest req) throws JspException;

	/**
	 * Gets the prefix for URLs for the non-SSL server.  This should always end with a /.
	 *
	 * @deprecated  Please use {@link HttpServletUtil#getAbsoluteURL(javax.servlet.http.HttpServletRequest, java.lang.String)}
	 *              as {@code HttpServletUtil.getAbsoluteURL(req, "/")}.
	 */
	@Deprecated
	public static String getDefaultUrlBase(HttpServletRequest req) {
		return HttpServletUtil.getAbsoluteURL(req, "/");
	}

	/**
	 * Gets the prefix for URLs for the server.  This should always end with a /.
	 */
	public String getUrlBase(HttpServletRequest req) throws JspException {
		return getDefaultUrlBase(req);
	}

	/**
	 * Configures the {@linkplain com.aoapps.web.resources.servlet.RegistryEE.Request request-scope web resources} that this skin uses.
	 * <p>
	 * Implementers should call <code>super.configureResources(…)</code> as a matter of convention, despite this default implementation doing nothing.
	 * </p>
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	public void configureResources(ServletContext servletContext, HttpServletRequest req, HttpServletResponse resp, Registry requestRegistry, PageAttributes page) {
		// Do nothing
	}

	/**
	 * Writes all of the HTML preceding the content of the page.
	 * <p>
	 * Both the {@link Serialization} and {@link Doctype} may have been set
	 * on the request, and these must be considered in the HTML generation.
	 * </p>
	 *
	 * @see SerializationEE#get(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest)
	 * @see DoctypeEE#get(javax.servlet.ServletContext, javax.servlet.ServletRequest)
	 */
	abstract public void startSkin(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, PageAttributes pageAttributes) throws JspException, IOException;

	/**
	 * Starts the content area of a page.  The content area provides additional features such as a nice border, and vertical and horizontal dividers.
	 */
	abstract public void startContent(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, PageAttributes pageAttributes, int[] colspans, String width) throws JspException, IOException;

	/**
	 * Prints an entire content line including the provided title.  The colspan should match the total colspan in startContent for proper appearance
	 */
	abstract public void printContentTitle(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, String title, int colspan) throws JspException, IOException;

	/**
	 * Starts one line of content with the initial colspan set to the provided colspan.
	 */
	abstract public void startContentLine(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, int colspan, String align, String width) throws JspException, IOException;

	/**
	 * Starts one line of content with the initial colspan set to the provided colspan.
	 */
	abstract public void printContentVerticalDivider(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, boolean visible, int colspan, int rowspan, String align, String width) throws JspException, IOException;

	/**
	 * Ends one line of content.
	 */
	abstract public void endContentLine(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, int rowspan, boolean endsInternal) throws JspException, IOException;

	/**
	 * Prints a horizontal divider of the provided colspans.
	 */
	abstract public void printContentHorizontalDivider(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, int[] colspansAndDirections, boolean endsInternal) throws JspException, IOException;

	/**
	 * Ends the content area of a page.
	 */
	abstract public void endContent(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, PageAttributes pageAttributes, int[] colspans) throws JspException, IOException;

	/**
	 * Writes all of the HTML following the content of the page,
	 * <p>
	 * Both the {@link Serialization} and {@link Doctype} may have been set
	 * on the request, and these must be considered in the HTML generation.
	 * </p>
	 *
	 * @see SerializationEE#get(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest)
	 * @see DoctypeEE#get(javax.servlet.ServletContext, javax.servlet.ServletRequest)
	 */
	abstract public void endSkin(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, PageAttributes pageAttributes) throws JspException, IOException;

	/**
	 * Begins a light area.
	 */
	abstract public void beginLightArea(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, String align, String width, boolean nowrap) throws JspException, IOException;

	/**
	 * Ends a light area.
	 */
	abstract public void endLightArea(HttpServletRequest req, HttpServletResponse resp, DocumentEE document) throws JspException, IOException;

	/**
	 * Begins a white area.
	 */
	abstract public void beginWhiteArea(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, String align, String width, boolean nowrap) throws JspException, IOException;

	/**
	 * Ends a white area.
	 */
	abstract public void endWhiteArea(HttpServletRequest req, HttpServletResponse resp, DocumentEE document) throws JspException, IOException;

	public static class Language {
		private final String code;
		private final String displayResourcesKey;
		private final String displayKey;
		private final String flagOnSrcResourcesKey;
		private final String flagOnSrcKey;
		private final String flagOffSrcResourcesKey;
		private final String flagOffSrcKey;
		private final String flagWidthResourcesKey;
		private final String flagWidthKey;
		private final String flagHeightResourcesKey;
		private final String flagHeightKey;
		private final AnyURI uri;

		/**
		 * @param uri the constant URL to use or {@code null} to have automatically set.
		 */
		public Language(
			String code,
			String displayResourcesKey,
			String displayKey,
			String flagOnSrcResourcesKey,
			String flagOnSrcKey,
			String flagOffSrcResourcesKey,
			String flagOffSrcKey,
			String flagWidthResourcesKey,
			String flagWidthKey,
			String flagHeightResourcesKey,
			String flagHeightKey,
			AnyURI uri
		) {
			this.code = code;
			this.displayResourcesKey = displayResourcesKey;
			this.displayKey = displayKey;
			this.flagOnSrcResourcesKey = flagOnSrcResourcesKey;
			this.flagOnSrcKey = flagOnSrcKey;
			this.flagOffSrcResourcesKey = flagOffSrcResourcesKey;
			this.flagOffSrcKey = flagOffSrcKey;
			this.flagWidthResourcesKey = flagWidthResourcesKey;
			this.flagWidthKey = flagWidthKey;
			this.flagHeightResourcesKey = flagHeightResourcesKey;
			this.flagHeightKey = flagHeightKey;
			this.uri = uri;
		}

		public String getCode() {
			return code;
		}

		public String getDisplay(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(displayResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+displayResourcesKey);
			return applicationResources.getMessage(locale, displayKey);
		}

		public String getFlagOnSrc(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(flagOnSrcResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+flagOnSrcResourcesKey);
			return applicationResources.getMessage(locale, flagOnSrcKey);
		}

		public String getFlagOffSrc(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(flagOffSrcResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+flagOffSrcResourcesKey);
			return applicationResources.getMessage(locale, flagOffSrcKey);
		}

		public int getFlagWidth(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(flagWidthResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+flagWidthResourcesKey);
			return Integer.parseInt(applicationResources.getMessage(locale, flagWidthKey));
		}

		public int getFlagHeight(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(flagHeightResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+flagHeightResourcesKey);
			return Integer.parseInt(applicationResources.getMessage(locale, flagHeightKey));
		}

		/**
		 * Gets the {@linkplain AnyURI URL} to use for this language or <code>null</code>
		 * to change language on the existing page.
		 */
		public AnyURI getUri() {
			return uri;
		}
	}

	/**
	 * Prints the auto index of all the page siblings.
	 */
	abstract public void printAutoIndex(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, PageAttributes pageAttributes) throws JspException, IOException;

	/**
	 * Begins a popup group.
	 */
	abstract public void beginPopupGroup(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, long groupId) throws JspException, IOException;

	/**
	 * Ends a popup group.
	 */
	abstract public void endPopupGroup(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, long groupId) throws JspException, IOException;

	/**
	 * Begins a popup that is in a popup group.
	 */
	abstract public void beginPopup(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, long groupId, long popupId, String width) throws JspException, IOException;

	/**
	 * Prints a popup close link/image/button for a popup that is part of a popup group.
	 */
	abstract public void printPopupClose(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, long groupId, long popupId) throws JspException, IOException;

	/**
	 * Ends a popup that is in a popup group.
	 */
	abstract public void endPopup(HttpServletRequest req, HttpServletResponse resp, DocumentEE document, long groupId, long popupId, String width) throws JspException, IOException;
}
