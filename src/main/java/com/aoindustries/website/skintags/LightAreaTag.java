/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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

import com.aoindustries.website.Skin;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * Writes the skin light area.
 *
 * @author  AO Industries, Inc.
 */
public class LightAreaTag extends PageAttributesBodyTag {

	private static final long serialVersionUID = 1L;

	private String width;
	private boolean nowrap;

	public LightAreaTag() {
		init();
	}

	private void init() {
		width = null;
		nowrap = false;
	}

	@Override
	public int doStartTag(PageAttributes pageAttributes) throws JspException {
		Skin skin = SkinTag.getSkin(pageContext);

		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		skin.beginLightArea((HttpServletRequest)pageContext.getRequest(), resp, pageContext.getOut(), width, nowrap);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag(PageAttributes pageAttributes) throws JspException {
		try {
			Skin skin = SkinTag.getSkin(pageContext);

			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.endLightArea((HttpServletRequest)pageContext.getRequest(), resp, pageContext.getOut());

			return EVAL_PAGE;
		} finally {
			init();
		}
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
