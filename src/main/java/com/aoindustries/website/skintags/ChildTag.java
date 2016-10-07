/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.util.Collection;
import java.util.Stack;
import javax.servlet.jsp.JspException;

/**
 * Adds a child to the hierarchy at the same level as this page.
 *
 * @author  AO Industries, Inc.
 */
public class ChildTag extends PageTag {

	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings("unchecked")
	protected int doEndTag(
		String title,
		String navImageAlt,
		String description,
		String author,
		String copyright,
		String path,
		String keywords,
		Collection<Meta> metas
	) throws JspException {
		Child child = new Child(title, navImageAlt, description, author, copyright, path, keywords, metas);
		Stack<ParentTag> stack = (Stack)pageContext.getRequest().getAttribute(ParentTag.STACK_ATTRIBUTE_NAME);
		if(stack==null || stack.isEmpty()) {
			PageAttributesBodyTag.getPageAttributes(pageContext).addChild(child);
		} else {
			stack.peek().addChild(child);
		}
		return EVAL_PAGE;
	}
}
