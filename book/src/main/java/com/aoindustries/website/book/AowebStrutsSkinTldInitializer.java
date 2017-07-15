/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.website.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import java.util.Collections;

/**
 * @author  AO Industries, Inc.
 */
public class AowebStrutsSkinTldInitializer extends TagReferenceInitializer {

	public AowebStrutsSkinTldInitializer() {
		super(
			"AOWeb Struts Skin Taglib Reference",
			"Skin Taglib Reference",
			"/aoweb-struts/core",
			"/aoweb-struts-skin.tld",
			AowebStrutsAowebTldInitializer.properties.getProperty("javac.link.javaApi.jdk16"),
			AowebStrutsAowebTldInitializer.properties.getProperty("javac.link.javaeeApi.6"),
			Collections.singletonMap("com.aoindustries.website.skintags.", AowebStrutsAowebTldInitializer.properties.getProperty("documented.url") + "apidocs/")
		);
	}
}
