/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
