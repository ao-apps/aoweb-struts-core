/*
 * Copyright 2007-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.taglib.AutoEncodingBufferedTag;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public class DescriptionTag extends AutoEncodingBufferedTag {

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return null;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws IOException {
		String description = capturedBody.trim().toString();
		JspTag parent = findAncestorWithClass(this, DescriptionAttribute.class);
		if(parent==null) {
			PageAttributesBodyTag.getPageAttributes((PageContext)getJspContext()).setDescription(description);
		} else {
			DescriptionAttribute descriptionAttribute = (DescriptionAttribute)parent;
			descriptionAttribute.setDescription(description);
		}
	}
}