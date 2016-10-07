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
 * Sets the copyright for the page.
 *
 * @author  AO Industries, Inc.
 */
public class CopyrightTag extends AutoEncodingBufferedTag {

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	@Override
	public MediaType getOutputType() {
		return null;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws IOException {
		String copyright = capturedBody.trim().toString();
		JspTag parent = findAncestorWithClass(this, CopyrightAttribute.class);
		if(parent==null) {
			PageAttributesBodyTag.getPageAttributes((PageContext)getJspContext()).setCopyright(copyright);
		} else {
			CopyrightAttribute copyrightAttribute = (CopyrightAttribute)parent;
			copyrightAttribute.setCopyright(copyright);
		}
	}
}
