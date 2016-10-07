/*
 * Copyright 2007-2013, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.taglib.AutoEncodingBufferedTag;
import com.aoindustries.taglib.HrefAttribute;
import com.aoindustries.taglib.RelAttribute;
import com.aoindustries.taglib.TypeAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTag
	extends AutoEncodingBufferedTag
	implements
		RelAttribute,
		HrefAttribute,
		TypeAttribute
{

	@Override
	public MediaType getContentType() {
		return MediaType.URL;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	private Object rel;
	private String href;
	private Object type;
	private String conditionalCommentExpression;

	public LinkTag() {
		init();
	}

	private void init() {
		rel = null;
		href = null;
		type = null;
		conditionalCommentExpression = null;
	}

	@Override
	public void setRel(Object rel) {
		this.rel = rel;
	}

	@Override
	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public void setType(Object type) {
		this.type = type;
	}

	public String getConditionalCommentExpression() {
		return conditionalCommentExpression;
	}

	public void setConditionalCommentExpression(String conditionalCommentExpression) {
		this.conditionalCommentExpression = conditionalCommentExpression;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws IOException {
		String myHref = href;
		if(myHref==null) myHref = capturedBody.trim().toString();
		PageAttributesBodyTag.getPageAttributes(
			(PageContext)getJspContext()
		).addLink(
			Coercion.toString(rel),
			myHref,
			Coercion.toString(type),
			conditionalCommentExpression
		);
	}
}
