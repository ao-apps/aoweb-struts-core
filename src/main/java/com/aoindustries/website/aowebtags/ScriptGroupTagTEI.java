/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2020  AO Industries, Inc.
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
package com.aoindustries.website.aowebtags;

import static com.aoindustries.website.Resources.PACKAGE_RESOURCES;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptGroupTagTEI extends TagExtraInfo {

	@Override
	public ValidationMessage[] validate(TagData data) {
		Object o = data.getAttribute("onloadMode");
		if(
			o != null
			&& o != TagData.REQUEST_TIME_VALUE
			&& !"none".equals(o)
			&& !"before".equals(o)
			&& !"after".equals(o)
		) {
			return new ValidationMessage[] {
				new ValidationMessage(
					data.getId(),
					PACKAGE_RESOURCES.getMessage(
						//"Invalid value for onloadMode, should be one of \"none\", \"before\", or \"after\": {0}",
						//Locale.getDefault(),
						"aowebtags.ScriptGroupTag.onloadMode.invalid",
						o
					)
				)
			};
		} else {
			return null;
		}
	}
}
