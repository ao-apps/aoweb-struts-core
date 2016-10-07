/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.util.Collection;

/**
 * Contains the information for a page used as a child.
 *
 * @author  AO Industries, Inc.
 */
public class Child extends Page {

	public Child(String title, String navImageAlt, String description, String author, String copyright, String path, String keywords, Collection<Meta> metas) {
		super(title, navImageAlt, description, author, copyright, path, keywords, metas);
	}
}
