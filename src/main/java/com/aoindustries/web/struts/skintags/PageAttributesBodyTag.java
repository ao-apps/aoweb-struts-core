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
package com.aoindustries.web.struts.skintags;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Resolves a shared instance of <code>PageAttributes</code> for all subclasses.
 *
 * @author  AO Industries, Inc.
 */
abstract public class PageAttributesBodyTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	public PageAttributesBodyTag() {
	}

	static PageAttributes getPageAttributes(HttpServletRequest request) {
		PageAttributes pageAttributes = (PageAttributes)request.getAttribute(PageAttributes.REQUEST_ATTRIBUTE);
		if(pageAttributes == null) {
			pageAttributes = new PageAttributes();
			request.setAttribute(PageAttributes.REQUEST_ATTRIBUTE, pageAttributes);
		}
		return pageAttributes;
	}

	static PageAttributes getPageAttributes(PageContext pageContext) {
		return getPageAttributes((HttpServletRequest)pageContext.getRequest());
	}

	/**
	 * @deprecated  You should probably be implementing in {@link #doStartTag(com.aoindustries.web.struts.skintags.PageAttributes)}
	 *
	 * @see  #doStartTag(com.aoindustries.web.struts.skintags.PageAttributes)
	 */
	@Deprecated
	@Override
	public int doStartTag() throws JspException {
		try {
			return doStartTag(getPageAttributes(pageContext));
		} catch(IOException err) {
			throw new JspTagException(err);
		}
	}

	public int doStartTag(PageAttributes pageAttributes) throws JspException, IOException {
		return EVAL_BODY_BUFFERED;
	}

	/* *
	 * @deprecated  You should probably be implementing in {@link #doAfterBody(com.aoindustries.web.struts.skintags.PageAttributes)}
	 *
	 * @see  #doAfterBody(com.aoindustries.web.struts.skintags.PageAttributes)
	 * /
	@Deprecated
	public int doAfterBody() throws JspException {
		return doAfterBody(getPageAttributes());
	}

	public int doAfterBody(PageAttributes pageAttributes) throws JspException, IOException {
		return SKIP_BODY;
	}*/

	/**
	 * @deprecated  You should probably be implementing in {@link #doEndTag(com.aoindustries.web.struts.skintags.PageAttributes)}
	 *
	 * @see  #doEndTag(com.aoindustries.web.struts.skintags.PageAttributes)
	 */
	@Deprecated
	@Override
	public int doEndTag() throws JspException {
		try {
			return doEndTag(getPageAttributes(pageContext));
		} catch(IOException err) {
			throw new JspTagException(err);
		}
	}

	public int doEndTag(PageAttributes pageAttributes) throws JspException, IOException {
		return EVAL_PAGE;
	}
}
