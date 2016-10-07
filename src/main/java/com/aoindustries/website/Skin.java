/*
 * Copyright 2007-2013, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.net.UrlUtils;
import com.aoindustries.website.skintags.PageAttributes;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import org.apache.struts.util.MessageResources;

/**
 * One look-and-feel for the website.
 *
 * @author  AO Industries, Inc.
 */
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
	 * Provides the correct character set for the given locale.
	 */
	static public String getCharacterSet(Locale locale) {
		/*if(locale!=null && locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
			//return "euc-jp";
			return "Shift_JIS";
		} else {*/
			// return "iso-8859-1";
			return "UTF-8";
		//}
	}

	/**
	 * Prints the links to the alternate translations of this page.
	 *
	 * {@link https://support.google.com/webmasters/answer/189077?hl=en}
	 */
	public static void printAlternativeLinks(HttpServletResponse resp, Appendable out, String fullPath, List<Language> languages) throws IOException {
		if(languages.size()>1) {
			// Default language
			{
				Language language = languages.get(0);
				out.append("    <link rel='alternate' hreflang='x-default' href='");
				String url = language.getUrl();
				encodeTextInXhtmlAttribute(
					resp.encodeURL(
						UrlUtils.encodeUrlPath(
							url==null
							? (fullPath+(fullPath.indexOf('?')==-1 ? "?" : "&")+"language="+language.getCode())
							: url,
							resp.getCharacterEncoding()
						)
					),
					out
				);
				out.append("' />\n");
			}
			// All languages
			for(Language language : languages) {
				out.append("    <link rel='alternate' hreflang='");
				encodeTextInXhtmlAttribute(language.getCode(), out);
				out.append("' href='");
				String url = language.getUrl();
				encodeTextInXhtmlAttribute(
					resp.encodeURL(
						UrlUtils.encodeUrlPath(
							url==null
							? (fullPath+(fullPath.indexOf('?')==-1 ? "?" : "&")+"language="+language.getCode())
							: url,
							resp.getCharacterEncoding()
						)
					),
					out
				);
				out.append("' />\n");
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
	 */
	public static String getDefaultUrlBase(HttpServletRequest req) throws JspException {
		int port = req.getServerPort();
		String contextPath = req.getContextPath();
		if(port!=80 && port!=443) {
			if(req.isSecure()) {
				// SSL development area
				return "https://"+req.getServerName()+":11156"+contextPath+"/";
			} else {
				// Non-SSL development area
				return "http://"+req.getServerName()+":8081"+contextPath+"/";
			}
		} else {
			if(req.isSecure()) {
				return "https://"+req.getServerName()+contextPath+"/";
			} else {
				return "http://"+req.getServerName()+contextPath+"/";
			}
		}
	}

	/**
	 * Gets the prefix for URLs for the server.  This should always end with a /.
	 */
	public String getUrlBase(HttpServletRequest req) throws JspException {
		return getDefaultUrlBase(req);
	}

	/**
	 * Writes the contents between the HTML tag and the page content (not including the HTML tag itself).
	 */
	abstract public void startSkin(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes) throws JspException;

	/**
	 * Starts the content area of a page.  The content area provides additional features such as a nice border, and vertical and horizontal dividers.
	 */
	abstract public void startContent(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes, int[] colspans, String width) throws JspException;

	/**
	 * Prints an entire content line including the provided title.  The colspan should match the total colspan in startContent for proper appearance
	 */
	abstract public void printContentTitle(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String title, int colspan) throws JspException;

	/**
	 * Starts one line of content with the initial colspan set to the provided colspan.
	 */
	abstract public void startContentLine(HttpServletRequest req, HttpServletResponse resp, JspWriter out, int colspan, String align, String width) throws JspException;

	/**
	 * Starts one line of content with the initial colspan set to the provided colspan.
	 */
	abstract public void printContentVerticalDivider(HttpServletRequest req, HttpServletResponse resp, JspWriter out, boolean visible, int colspan, int rowspan, String align, String width) throws JspException;

	/**
	 * Ends one line of content.
	 */
	abstract public void endContentLine(HttpServletRequest req, HttpServletResponse resp, JspWriter out, int rowspan, boolean endsInternal) throws JspException;

	/**
	 * Prints a horizontal divider of the provided colspans.
	 */
	abstract public void printContentHorizontalDivider(HttpServletRequest req, HttpServletResponse resp, JspWriter out, int[] colspansAndDirections, boolean endsInternal) throws JspException;

	/**
	 * Ends the content area of a page.
	 */
	abstract public void endContent(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes, int[] colspans) throws JspException;

	/**
	 * Writes the contents between the page content and the HTML tag (not including the HTML tag itself).
	 */
	abstract public void endSkin(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes) throws JspException;

	/**
	 * Begins a light area.
	 */
	abstract public void beginLightArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String width, boolean nowrap) throws JspException;

	/**
	 * Ends a light area.
	 */
	abstract public void endLightArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out) throws JspException;

	/**
	 * Begins a white area.
	 */
	abstract public void beginWhiteArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String width, boolean nowrap) throws JspException;

	/**
	 * Ends a white area.
	 */
	abstract public void endWhiteArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out) throws JspException;

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
		private final String url;

		/**
		 * @param url the constant URL to use or <code>null</code> to have automatically set.
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
			String url
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
			this.url = url;
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

		public String getFlagWidth(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(flagWidthResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+flagWidthResourcesKey);
			return applicationResources.getMessage(locale, flagWidthKey);
		}

		public String getFlagHeight(HttpServletRequest req, Locale locale) throws JspException {
			MessageResources applicationResources = (MessageResources)req.getAttribute(flagHeightResourcesKey);
			if(applicationResources==null) throw new JspException("Unable to load resources: "+flagHeightResourcesKey);
			return applicationResources.getMessage(locale, flagHeightKey);
		}

		/**
		 * Gets the absolute URL to use for this language or <code>null</code>
		 * to change language on the existing page.
		 */
		public String getUrl() {
			return url;
		}
	}

	/**
	 * Prints the auto index of all the page siblings.
	 */
	abstract public void printAutoIndex(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes) throws JspException;

	/**
	 * Begins a popup group.
	 */
	abstract public void beginPopupGroup(HttpServletRequest req, JspWriter out, long groupId) throws JspException;

	/**
	 * Ends a popup group.
	 */
	abstract public void endPopupGroup(HttpServletRequest req, JspWriter out, long groupId) throws JspException;

	/**
	 * Begins a popup that is in a popup group.
	 */
	abstract public void beginPopup(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String width) throws JspException;

	/**
	 * Prints a popup close link/image/button for a popup that is part of a popup group.
	 */
	abstract public void printPopupClose(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId) throws JspException;

	/**
	 * Ends a popup that is in a popup group.
	 */
	abstract public void endPopup(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String width) throws JspException;
}
