/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2019, 2021  AO Industries, Inc.
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

import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.html.servlet.FlowContent;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * Writes the skin white area.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Convert these to TryCatchFinally when have init()
public class WhiteAreaTag extends PageAttributesBodyTag {

	private static final long serialVersionUID = 1L;

	private String align;
	private String width;
	private boolean nowrap;
	private FlowContent<?> whiteArea;

	public WhiteAreaTag() {
		init();
	}

	private void init() {
		align = null;
		width = null;
		nowrap = false;
		whiteArea = null;
	}

	@Override
	public int doStartTag(PageAttributes pageAttributes) throws JspException, IOException {
		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		whiteArea = SkinTag.getSkin(pageContext).beginWhiteArea(
			req,
			resp,
			new DocumentEE(
				pageContext.getServletContext(),
				req,
				resp,
				pageContext.getOut(),
				false, // Do not add extra newlines to JSP
				false  // Do not add extra indentation to JSP
			),
			align,
			width,
			nowrap
		);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag(PageAttributes pageAttributes) throws JspException, IOException {
		try {
			assert whiteArea != null;
			SkinTag.getSkin(pageContext).endWhiteArea(
				(HttpServletRequest)pageContext.getRequest(),
				(HttpServletResponse)pageContext.getResponse(),
				whiteArea
			);
			return EVAL_PAGE;
		} finally {
			init();
		}
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public boolean getNowrap() {
		return nowrap;
	}

	public void setNowrap(boolean nowrap) {
		this.nowrap = nowrap;
	}
}
