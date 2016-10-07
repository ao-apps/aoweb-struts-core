/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.util.Collection;
import java.util.Collections;

/**
 * Contains the information for a page used for parents and children.
 *
 * @author  AO Industries, Inc.
 */
abstract public class Page {

	private final String title;
	private final String navImageAlt;
	private final String description;
	private final String author;
	private final String copyright;
	private final String path;
	private final String keywords;
	private final Collection<Meta> metas;

	public Page(String title, String navImageAlt, String description, String author, String copyright, String path, String keywords, Collection<Meta> metas) {
		if(title==null) throw new IllegalArgumentException("title is null");
		if(description==null) throw new IllegalArgumentException("description is null");
		if(path==null) throw new IllegalArgumentException("path is null");
		//if(keywords==null) throw new IllegalArgumentException("keywords is null");
		this.title = title;
		this.navImageAlt = navImageAlt;
		this.description = description;
		this.author = author;
		this.copyright = copyright;
		this.path = path;
		this.keywords = keywords;
		this.metas = metas;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * If not specified, the nav image defaults to the title.
	 */
	public String getNavImageAlt() {
		String myNavImageAlt = this.navImageAlt;
		if(myNavImageAlt==null || myNavImageAlt.length()==0) myNavImageAlt = this.title;
		return myNavImageAlt;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}

	public String getCopyright() {
		return copyright;
	}

	public String getPath() {
		return path;
	}

	public String getKeywords() {
		return keywords;
	}

	public Collection<Meta> getMetas() {
		if(metas==null) return Collections.emptyList();
		return metas;
	}

	@Override
	public String toString() {
		return title;
	}
}
