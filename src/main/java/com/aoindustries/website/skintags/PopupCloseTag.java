/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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
