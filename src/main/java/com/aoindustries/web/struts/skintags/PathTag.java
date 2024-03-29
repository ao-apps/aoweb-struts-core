/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2013, 2016, 2019, 2020, 2021  AO Industries, Inc.
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
import com.aoapps.net.URIParametersMap;
import com.aoapps.net.URIParametersUtils;
import com.aoapps.taglib.ParamsAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Sets the path for a page or its PathAttribute parent.
 *
 * @author  AO Industries, Inc.
 */
public class PathTag extends EncodingBufferedTag implements ParamsAttribute {

	private URIParametersMap params;

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
		// TODO: Find a way to validate content only after trimming, then use: return MediaType.URL;
	}

	@Override
	public MediaType getOutputType() {
		return null;
	}

	@Override
	public void addParam(String name, Object value) {
		if(params == null) params = new URIParametersMap();
		params.add(name, value);
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		String path = capturedBody.trim().toString();
		path = URIParametersUtils.addParams(path, params);
		PageTag pageTag = PageTag.getPageTag(pageContext.getRequest());
		if(pageTag==null) {
			PageAttributesBodyTag.getPageAttributes(pageContext).setPath(path);
		} else {
			pageTag.setPath(path);
		}
	}
}
