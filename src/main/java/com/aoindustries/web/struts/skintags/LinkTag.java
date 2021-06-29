/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2013, 2015, 2016, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts.skintags;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.EncodingBufferedTag;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.taglib.HrefAttribute;
import com.aoapps.taglib.RelAttribute;
import com.aoapps.taglib.TypeAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTag extends EncodingBufferedTag
	implements
		RelAttribute,
		HrefAttribute,
		TypeAttribute
{

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
		// TODO: Find a way to validate content only after trimming, then use: return MediaType.URL;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	private String rel;
	private String href;
	private String type;

	public LinkTag() {
		init();
	}

	private void init() {
		rel = null;
		href = null;
		type = null;
	}

	@Override
	public void setRel(String rel) {
		this.rel = rel;
	}

	@Override
	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		String myHref = href;
		if(myHref==null) myHref = capturedBody.trim().toString();
		PageAttributesBodyTag.getPageAttributes(
			(PageContext)getJspContext()
		).addLink(
			rel,
			myHref,
			type
		);
	}
}
