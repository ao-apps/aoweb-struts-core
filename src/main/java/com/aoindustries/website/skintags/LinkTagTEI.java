/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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

import com.aoindustries.website.ApplicationResources;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTagTEI extends TagExtraInfo {

	@Override
	public ValidationMessage[] validate(TagData data) {
		Object o = data.getAttribute("conditionalCommentExpression");
		if(
			o != null
			&& o != TagData.REQUEST_TIME_VALUE
		) {
			if(PageAttributes.Link.isValidConditionalCommentExpression((String)o)) return null;
			else {
				return new ValidationMessage[] {
					new ValidationMessage(
						data.getId(),
						ApplicationResources.accessor.getMessage(
							//"Invalid value for conditional comment expression.  Please refer to aoweb-struts-skin.tld for the valid values.",
							//Locale.getDefault(),
							"skintags.LinkTagTEI.validate.layout.invalid"
						)
					)
				};
			}
		} else {
			return null;
		}
	}
}
