/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2020, 2021  AO Industries, Inc.
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
import com.aoapps.servlet.jsp.tagext.JspTagUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class ContentVerticalDividerTag extends TagSupport {

	public static final String TAG_NAME = "<skin:contentVerticalDivider>";

	private static final long serialVersionUID = 1L;

	private boolean visible;
	private int colspan;
	private int rowspan;
	private String align;
	private String width;

	public ContentVerticalDividerTag() {
		init();
	}

	private void init() {
		this.visible = true;
		this.colspan = 1;
		this.rowspan = 1;
		this.align = null;
		this.width = null;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			ContentLineTag contentLineTag = JspTagUtils.requireAncestor(TAG_NAME, this, ContentLineTag.TAG_NAME, ContentLineTag.class);
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			SkinTag.getSkin(pageContext).printContentVerticalDivider(
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
				visible,
				colspan,
				rowspan,
				align,
				width
			);
			contentLineTag.setLastRowSpan(rowspan);
			return SKIP_BODY;
		} catch(IOException err) {
			throw new JspTagException(err);
		} finally {
			init(); // TODO: TryCatchFinally
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
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
}
