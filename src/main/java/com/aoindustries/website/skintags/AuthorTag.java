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

/**
 * @author  AO Industries, Inc.
 */
public class AuthorTag extends AutoEncodingBufferedTag {

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
		PageContext pageContext = (PageContext)getJspContext();
		String author = capturedBody.trim().toString();
		PageTag pageTag = PageTag.getPageTag(pageContext.getRequest());
		if(pageTag==null) {
			PageAttributesBodyTag.getPageAttributes(pageContext).setAuthor(author);
		} else {
			pageTag.setAuthor(author);
		}
	}
}
