/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains the information for a page used for parents.
 *
 * @author  AO Industries, Inc.
 */
public class Parent extends Page {

	private final List<Child> children;

	public Parent(String title, String navImageAlt, String description, String author, String copyright, String path, String keywords, Collection<Meta> metas, List<Child> children) {
		super(title, navImageAlt, description, author, copyright, path, keywords, metas);
		this.children = children;
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
}
