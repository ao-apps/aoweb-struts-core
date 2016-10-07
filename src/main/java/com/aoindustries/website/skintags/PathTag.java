/*
 * Copyright 2007-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.taglib.AutoEncodingBufferedTag;
import com.aoindustries.taglib.ParamsAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspTag;

/**
 * Sets the path for a page or its PathAttribute parent.
 *
 * @author  AO Industries, Inc.
 */
public class PathTag extends AutoEncodingBufferedTag implements ParamsAttribute {

	private static final long serialVersionUID = 1L;

	private HttpParametersMap params;

	@Override
	public MediaType getContentType() {
		return MediaType.URL;
	}

	@Override
	public MediaType getOutputType() {
		return null;
	}

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new HttpParametersMap();
		params.addParameter(name, value);
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws IOException {
		PageContext pageContext = (PageContext)getJspContext();
		ServletResponse response = pageContext.getResponse();
		String path = capturedBody.trim().toString();
		path = HttpParametersUtils.addParams(path, params, response.getCharacterEncoding());
		JspTag parent = findAncestorWithClass(this, PathAttribute.class);
		if(parent==null) {
			PageAttributesBodyTag.getPageAttributes(pageContext).setPath(path);
		} else {
			PathAttribute pathAttribute = (PathAttribute)parent;
			pathAttribute.setPath(path);
		}
	}
}
