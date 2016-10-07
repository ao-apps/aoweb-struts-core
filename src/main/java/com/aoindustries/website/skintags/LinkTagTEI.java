/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
