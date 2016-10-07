/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Resolves a shared instance of <code>PageAttributes</code> for all subclasses.
 *
 * @author  AO Industries, Inc.
 */
abstract public class PageAttributesTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	public PageAttributesTag() {
	}

	@Override
	final public int doStartTag() throws JspException {
		return doStartTag(PageAttributesBodyTag.getPageAttributes(pageContext));
	}

	public int doStartTag(PageAttributes pageAttributes) throws JspException {
		return SKIP_BODY;
	}

	@Override
	final public int doEndTag() throws JspException {
		return doEndTag(PageAttributesBodyTag.getPageAttributes(pageContext));
	}

	public int doEndTag(PageAttributes pageAttributes) throws JspException {
		return EVAL_PAGE;
	}
}
