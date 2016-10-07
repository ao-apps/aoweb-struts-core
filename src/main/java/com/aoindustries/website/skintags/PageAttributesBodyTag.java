/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
