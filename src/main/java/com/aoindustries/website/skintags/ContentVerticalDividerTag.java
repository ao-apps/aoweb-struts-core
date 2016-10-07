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
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * @author  AO Industries, Inc.
 */
public class ContentVerticalDividerTag extends TagSupport {

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
			ContentLineTag contentLineTag = (ContentLineTag)findAncestorWithClass(this, ContentLineTag.class);
			if(contentLineTag==null) {
				HttpSession session = pageContext.getSession();
				Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
				MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
				throw new JspException(applicationResources.getMessage(locale, "skintags.ContentVerticalDividerTag.mustNestInContentLineTag"));
			}

			Skin skin = SkinTag.getSkin(pageContext);

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.printContentVerticalDivider(req, resp, pageContext.getOut(), visible, colspan, rowspan, align, width);

			contentLineTag.setLastRowSpan(rowspan);

			return SKIP_BODY;
		} finally {
			init();
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
