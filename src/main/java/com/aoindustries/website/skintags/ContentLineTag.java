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

import com.aoindustries.servlet.jsp.tagext.JspTagUtils;
import com.aoindustries.website.Skin;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class ContentLineTag extends BodyTagSupport {

	public static final String TAG_NAME = "<skin:contentLine>";

	private static final long serialVersionUID = 1L;

	private int colspan;
	private String align;
	private String width;
	private boolean endsInternal;
	private int lastRowSpan;

	public ContentLineTag() {
		init();
	}

	private void init() {
		this.colspan = 1;
		this.align = null;
		this.width = null;
		this.endsInternal = false;
		this.lastRowSpan = 1;
	}

	@Override
	public int doStartTag() throws JspException {
		JspTagUtils.requireAncestor(TAG_NAME, this, ContentTag.TAG_NAME, ContentTag.class);

		Skin skin = SkinTag.getSkin(pageContext);

		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		skin.startContentLine(req, resp, pageContext.getOut(), colspan, align, width);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			Skin skin = SkinTag.getSkin(pageContext);

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.endContentLine(req, resp, pageContext.getOut(), lastRowSpan, endsInternal);

			return EVAL_PAGE;
		} finally {
			init();
		}
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
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

	public boolean isEndsInternal() {
		return endsInternal;
	}

	public void setEndsInternal(boolean endsInternal) {
		this.endsInternal = endsInternal;
	}

	/**
	 * The row span on endContentLine either either 1 or the rowspan of the last contentVerticalDivider
	 */
	void setLastRowSpan(int lastRowSpan) {
		this.lastRowSpan = lastRowSpan;
	}
}
