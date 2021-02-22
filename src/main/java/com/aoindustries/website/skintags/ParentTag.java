/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2019, 2020, 2021  AO Industries, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

/**
 * Adds a parent to the hierarchy above this page.
 *
 * @author  AO Industries, Inc.
 */
public class ParentTag extends PageTag {

	private static final long serialVersionUID = 1L;

	static final String STACK_REQUEST_ATTRIBUTE = ParentTag.class.getName() + ".stack";

	private List<Child> children;

	@Override
	protected void init() {
		super.init();
		children = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		ServletRequest request = pageContext.getRequest();
		Stack<ParentTag> stack = (Stack)request.getAttribute(STACK_REQUEST_ATTRIBUTE);
		if(stack==null) request.setAttribute(STACK_REQUEST_ATTRIBUTE, stack = new Stack<>());
		stack.push(this);
		return super.doStartTag();
	}

	/**
	 * Gets the children of this parent page.
	 */
	public List<Child> getChildren() {
		if(children == null) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(children);
		}
	}

	public void addChild(Child child) {
		if(children == null) children = new ArrayList<>();
		children.add(child);
	}

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
	) throws JspException, IOException {
		Stack<ParentTag> stack = (Stack)pageContext.getRequest().getAttribute(STACK_REQUEST_ATTRIBUTE);
		if(stack!=null && !stack.isEmpty() && stack.peek()==this) stack.pop();

		PageAttributesBodyTag.getPageAttributes(pageContext).addParent(
			new Parent(title, navImageAlt, description, author, authorHref, copyright, path, keywords, metas, children)
		);
		return EVAL_PAGE;
	}
}
