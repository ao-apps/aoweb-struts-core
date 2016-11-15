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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
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

	static PageAttributes getPageAttributes(PageContext pageContext) {
		PageAttributes pageAttributes = (PageAttributes)pageContext.getAttribute(PageAttributes.ATTRIBUTE_KEY, PageAttributes.ATTRIBUTE_SCOPE);
		if(pageAttributes==null) {
			pageAttributes = new PageAttributes((HttpServletRequest)pageContext.getRequest());
			pageContext.setAttribute(PageAttributes.ATTRIBUTE_KEY, pageAttributes, PageAttributes.ATTRIBUTE_SCOPE);
		}
		return pageAttributes;
	}

	@Override
	final public int doStartTag() throws JspException {
		return doStartTag(getPageAttributes(pageContext));
	}

	public int doStartTag(PageAttributes pageAttributes) throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	/*
	final public int doAfterBody() throws JspException {
		return doAfterBody(getPageAttributes());
	}

	public int doAfterBody(PageAttributes pageAttributes) throws JspException {
		return SKIP_BODY;
	}*/

	@Override
	final public int doEndTag() throws JspException {
		return doEndTag(getPageAttributes(pageContext));
	}

	public int doEndTag(PageAttributes pageAttributes) throws JspException {
		return EVAL_PAGE;
	}
}
