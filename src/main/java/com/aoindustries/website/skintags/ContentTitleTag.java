/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.taglib.EncodingBufferedTag;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.website.Skin;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * @author  AO Industries, Inc.
 */
public class ContentTitleTag extends EncodingBufferedTag {

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		String title = capturedBody.trim().toString();

		ContentTag contentTag = (ContentTag)findAncestorWithClass(this, ContentTag.class);
		if(contentTag==null) {
			HttpSession session = pageContext.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = (MessageResources)req.getAttribute("/ApplicationResources");
			throw new JspException(applicationResources.getMessage(locale, "skintags.ContentTitleTag.mustNestInContentTag"));
		}

		Skin skin = SkinTag.getSkin(pageContext);

		int[] colspans = contentTag.getColspansParsed();
		int totalColspan = 0;
		for(int c=0;c<colspans.length;c++) totalColspan += colspans[c];

		skin.printContentTitle(req, resp, pageContext.getOut(), title, totalColspan);
	}
}
