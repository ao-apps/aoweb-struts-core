/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.util.StringUtility;
import com.aoindustries.website.Skin;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class ContentTag extends PageAttributesBodyTag {

	private static final long serialVersionUID = 1L;

	/**
	 * Parses a String of comma-separated integers into an <code>int[]</code>.
	 */
	static int[] parseColspans(String colspans) {
		List<String> tokens = StringUtility.splitStringCommaSpace(colspans);
		int[] array = new int[tokens.size()];
		for(int c=0;c<tokens.size();c++) array[c]=Integer.parseInt(tokens.get(c));
		return array;
	}

	private String colspans;
	private int[] colspansParsed;
	private String width;

	public ContentTag() {
		init();
	}

	private void init() {
		colspans = "1";
		colspansParsed = new int[] {1};
		width = null;
	}

	@Override
	public int doStartTag(PageAttributes pageAttributes) throws JspException {
		Skin skin = SkinTag.getSkin(pageContext);

		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		skin.startContent(req, resp, pageContext.getOut(), pageAttributes, colspansParsed, width);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag(PageAttributes pageAttributes) throws JspException {
		try {
			Skin skin = SkinTag.getSkin(pageContext);

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.endContent(req, resp, pageContext.getOut(), pageAttributes, colspansParsed);

			return EVAL_PAGE;
		} finally {
			init();
		}
	}

	public String getColspans() {
		return colspans;
	}

	int[] getColspansParsed() {
		return colspansParsed;
	}

	public void setColspans(String colspans) {
		this.colspans = colspans;
		this.colspansParsed = parseColspans(colspans);
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}
}
