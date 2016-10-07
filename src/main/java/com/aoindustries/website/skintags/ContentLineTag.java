/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.website.Skin;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * @author  AO Industries, Inc.
 */
public class ContentLineTag extends BodyTagSupport {

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
		ContentTag contentTag = (ContentTag)findAncestorWithClass(this, ContentTag.class);
		if(contentTag==null) {
			HttpSession session = pageContext.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
			throw new JspException(applicationResources.getMessage(locale, "skintags.ContentLineTag.mustNestInContentTag"));
		}

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
