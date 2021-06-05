/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.website;

/**
 * Provides a simplified interface for obtaining localized values from the SiteApplicationResources.properties files.
 * <p>
 * Each website is expected to provide its own version of these per-site resources.  Furthermore, each website defines
 * its own <code>devel/</code> sub-project for in-context translations.
 * </p>
 */
final public class SiteResources {

	public static final com.aoapps.lang.i18n.Resources SITE_RESOURCES =
		com.aoapps.lang.i18n.Resources.getResources(SiteResources.class.getPackage(), "SiteApplicationResources");

	/**
	 * Make no instances.
	 */
	private SiteResources() {}
}
