/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.website.Skin;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * Renders a popup close link/image/button.  Must be nested inside a PopupTag.
 *
 * @see  PopupTag
 *
 * @author  AO Industries, Inc.
 */
public class PopupCloseTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	public PopupCloseTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		Skin skin = SkinTag.getSkin(pageContext);

		// Look for the containing popup tag
		PopupTag popupTag = (PopupTag)findAncestorWithClass(this, PopupTag.class);
		if(popupTag==null) {
			HttpSession session = pageContext.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
			throw new JspException(applicationResources.getMessage(locale, "skintags.PopupCloseTag.mustNestInPopupTag"));
		}

		// Look for containing popupGroup tag
		PopupGroupTag popupGroupTag = (PopupGroupTag)findAncestorWithClass(popupTag, PopupGroupTag.class);
		if(popupGroupTag==null) {
			HttpSession session = pageContext.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
			throw new JspException(applicationResources.getMessage(locale, "skintags.PopupTag.mustNestInPopupGroupTag"));
		} else {
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.printPopupClose((HttpServletRequest)pageContext.getRequest(), resp, pageContext.getOut(), popupGroupTag.sequenceId, popupTag.sequenceId);
		}
		return SKIP_BODY;
	}
}
