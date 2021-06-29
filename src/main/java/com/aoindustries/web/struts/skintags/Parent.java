/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2019  AO Industries, Inc.
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
package com.aoindustries.web.struts.skintags;

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

	public Parent(String title, String navImageAlt, String description, String author, String authorHref, String copyright, String path, String keywords, Collection<Meta> metas, List<Child> children) {
		super(title, navImageAlt, description, author, authorHref, copyright, path, keywords, metas);
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
