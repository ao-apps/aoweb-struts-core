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

import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.website.Resources.PACKAGE_RESOURCES;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Common parent to parent and child tags.
 *
 * @author  AO Industries, Inc.
 */
abstract public class PageTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * Request-scope attribute containing the current page, used to find the
	 * parent PageTag.
	 */
	static final String PAGE_TAG_ATTRIBUTE = PageTag.class.getName();

	/**
	 * Gets the current page tag (parent or child).
	 */
	static PageTag getPageTag(ServletRequest request) {
		return (PageTag)request.getAttribute(PAGE_TAG_ATTRIBUTE);
	}

	private String title;
	private String navImageAlt;
	private String description;
	private String author;
	private String authorHref;
	private String copyright;
	private String path;
	private String keywords;
	private Collection<Meta> metas;

	public PageTag() {
		init(); // TODO: Switch to TryCatchFinally style, review all
	}

	protected void init() {
		title = null;
		navImageAlt = null;
		description = null;
		author = null;
		authorHref = null;
		copyright = null;
		path = null;
		keywords = null;
		metas = null;
	}

	private Object oldPageTag;

	@Override
	public int doStartTag() throws JspException {
		ServletRequest request = pageContext.getRequest();
		oldPageTag = request.getAttribute(PAGE_TAG_ATTRIBUTE);
		request.setAttribute(PAGE_TAG_ATTRIBUTE, this);
		return EVAL_BODY_BUFFERED;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setNavImageAlt(String navImageAlt) {
		this.navImageAlt = navImageAlt;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setAuthorHref(String authorHref) {
		this.authorHref = authorHref;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public void addMeta(Meta meta) {
		if(metas==null) metas = new ArrayList<>();
		metas.add(meta);
	}

	@Override
	public int doEndTag() throws JspException {
		ServletRequest request = pageContext.getRequest();
		try {
			if(title==null) {
				throw new LocalizedJspTagException(PACKAGE_RESOURCES, "skintags.PageTag.needsTitleTag");
			}
			String myNavImageAlt = this.navImageAlt;
			if(myNavImageAlt == null || myNavImageAlt.length()==0) myNavImageAlt=title;
			String myDescription = this.description;
			if(myDescription == null || myDescription.length()==0) myDescription=title;
			return doEndTag(title, myNavImageAlt, myDescription, author, authorHref, copyright, path, keywords, metas);
		} finally {
			request.setAttribute(PAGE_TAG_ATTRIBUTE, oldPageTag);
			init();
		}
	}

	abstract protected int doEndTag(
		String title,
		String navImageAlt,
		String description,
		String author,
		String authorHref,
		String copyright,
		String path,
		String keywords,
		Collection<Meta> metas
	) throws JspException;
}
