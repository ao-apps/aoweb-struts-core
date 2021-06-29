/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2020  AO Industries, Inc.
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
package com.aoindustries.web.struts.skintags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class LightDarkTableRowTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	private String pageAttributeId;

	public LightDarkTableRowTag() {
		init();
	}

	public String getPageAttributeId() {
		return pageAttributeId;
	}

	public void setPageAttributeId(String pageAttributeId) {
		this.pageAttributeId = pageAttributeId;
	}

	private void init() {
		// Always start with a light row
		pageAttributeId = "LightDarkTableRowTag.isDark";
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			out.write("<tr class=\"");
			Boolean isDark = (Boolean)pageContext.getAttribute(pageAttributeId, PageContext.PAGE_SCOPE);
			if(isDark==null) pageContext.setAttribute(pageAttributeId, isDark = Boolean.FALSE, PageContext.PAGE_SCOPE);
			out.write(isDark ? "aoDarkRow" : "aoLightRow");
			out.write("\">");
			return EVAL_BODY_INCLUDE;
		} catch(IOException err) {
			throw new JspTagException(err);
		}
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			Boolean isDark = (Boolean)pageContext.getAttribute(pageAttributeId, PageContext.PAGE_SCOPE);
			if(isDark==null) isDark = Boolean.FALSE;
			pageContext.setAttribute(pageAttributeId, !isDark, PageContext.PAGE_SCOPE);
			pageContext.getOut().write("</tr>");
			return EVAL_PAGE;
		} catch(IOException err) {
			throw new JspTagException(err);
		} finally {
			init();
		}
	}
}
