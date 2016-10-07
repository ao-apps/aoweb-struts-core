/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * Common parent to parent and child tags.
 *
 * @author  AO Industries, Inc.
 */
abstract public class PageTag extends BodyTagSupport implements
		KeywordsAttribute,
		DescriptionAttribute,
		AuthorAttribute,
		CopyrightAttribute,
		PathAttribute,
		MetasAttribute,
		TitleAttribute,
		NavImageAltAttribute {

	private static final long serialVersionUID = 1L;

	private String title;
	private String navImageAlt;
	private String description;
	private String author;
	private String copyright;
	private String path;
	private String keywords;
	private Collection<Meta> metas;

	public PageTag() {
		init();
	}

	protected void init() {
		title = null;
		navImageAlt = null;
		description = null;
		author = null;
		copyright = null;
		path = null;
		keywords = null;
		metas = null;
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getNavImageAlt() {
		return navImageAlt;
	}

	@Override
	public void setNavImageAlt(String navImageAlt) {
		this.navImageAlt = navImageAlt;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String getCopyright() {
		return copyright;
	}

	@Override
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getKeywords() {
		return keywords;
	}

	@Override
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Override
	public Collection<Meta> getMetas() {
		if(metas==null) return Collections.emptyList();
		return metas;
	}

	@Override
	public void addMeta(Meta meta) {
		if(metas==null) metas = new ArrayList<Meta>();
		metas.add(meta);
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if(title==null) {
				HttpSession session = pageContext.getSession();
				Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
				MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
				throw new JspException(applicationResources.getMessage(locale, "skintags.PageTag.needsTitleTag"));
			}
			String myNavImageAlt = this.navImageAlt;
			if(myNavImageAlt == null || myNavImageAlt.length()==0) myNavImageAlt=title;
			String myDescription = this.description;
			if(myDescription == null || myDescription.length()==0) myDescription=title;
			return doEndTag(title, myNavImageAlt, myDescription, author, copyright, path, keywords, metas);
		} finally {
			init();
		}
	}

	abstract protected int doEndTag(
		String title,
		String navImageAlt,
		String description,
		String author,
		String copyright,
		String path,
		String keywords,
		Collection<Meta> metas
	) throws JspException;
}
