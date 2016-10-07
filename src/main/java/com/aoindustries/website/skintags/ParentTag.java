/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

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

	static final String STACK_ATTRIBUTE_NAME = ParentTag.class.getName()+".stack";

	private List<Child> children;

	@Override
	protected void init() {
		super.init();
		children = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public int doStartTag() {
		ServletRequest request = pageContext.getRequest();
		Stack<ParentTag> stack = (Stack)request.getAttribute(STACK_ATTRIBUTE_NAME);
		if(stack==null) request.setAttribute(STACK_ATTRIBUTE_NAME, stack = new Stack<ParentTag>());
		stack.push(this);
		return super.doStartTag();
	}

	/**
	 * Gets the children of this parent page.
	 */
	public List<Child> getChildren() {
		if(children==null) {
			List<Child> emptyList = Collections.emptyList();
			return emptyList;
		}
		return children;
	}

	public void addChild(Child child) {
		if(children==null) children = new ArrayList<Child>();
		children.add(child);
	}

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
		Stack<ParentTag> stack = (Stack)pageContext.getRequest().getAttribute(STACK_ATTRIBUTE_NAME);
		if(stack!=null && !stack.isEmpty() && stack.peek()==this) stack.pop();

		PageAttributesBodyTag.getPageAttributes(pageContext).addParent(
			new Parent(title, navImageAlt, description, author, copyright, path, keywords, metas, children)
		);
		return EVAL_PAGE;
	}
}
