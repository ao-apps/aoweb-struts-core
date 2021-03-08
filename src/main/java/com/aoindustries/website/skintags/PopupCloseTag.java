/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.servlet.jsp.tagext.JspTagUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Renders a popup close link/image/button.  Must be nested inside a PopupTag.
 *
 * @see  PopupTag
 *
 * @author  AO Industries, Inc.
 */
public class PopupCloseTag extends TagSupport {

	public static final String TAG_NAME = "<skin:popupClose>";

	private static final long serialVersionUID = 1L;

	public PopupCloseTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			// Look for the containing popup tag
			PopupTag popupTag = JspTagUtils.requireAncestor(TAG_NAME, this, PopupTag.TAG_NAME, PopupTag.class);

			// Look for containing popupGroup tag
			PopupGroupTag popupGroupTag = JspTagUtils.requireAncestor(PopupTag.TAG_NAME, popupTag, PopupGroupTag.TAG_NAME, PopupGroupTag.class);

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			SkinTag.getSkin(pageContext).printPopupClose(
				req,
				resp,
				DocumentEE.get(
					pageContext.getServletContext(),
					req,
					resp,
					pageContext.getOut(),
					false, // Do not add extra newlines to JSP
					false  // Do not add extra indentation to JSP
				),
				popupGroupTag.sequenceId,
				popupTag.sequenceId
			);
			return SKIP_BODY;
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}
}
