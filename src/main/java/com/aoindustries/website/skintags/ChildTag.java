/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2019, 2020  AO Industries, Inc.
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
		String authorHref,
		String copyright,
		String path,
		String keywords,
		Collection<Meta> metas
	) throws JspException {
		Child child = new Child(title, navImageAlt, description, author, authorHref, copyright, path, keywords, metas);
		Stack<ParentTag> stack = (Stack)pageContext.getRequest().getAttribute(ParentTag.STACK_REQUEST_ATTRIBUTE);
		if(stack==null || stack.isEmpty()) {
			PageAttributesBodyTag.getPageAttributes(pageContext).addChild(child);
		} else {
			stack.peek().addChild(child);
		}
		return EVAL_PAGE;
	}
}
