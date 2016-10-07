/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
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
			throw new JspException(err);
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
			throw new JspException(err);
		} finally {
			init();
		}
	}
}
