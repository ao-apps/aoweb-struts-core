/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.aowebtags;

import com.aoindustries.servlet.ServletContextCache;
import java.net.MalformedURLException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Evaluates the body if the provided resource exists.
 *
 * @author  AO Industries, Inc.
 */
public class ExistsTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	private String path;

	public ExistsTag() {
		init();
	}

	private void init() {
		path = null;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			return ServletContextCache.getResource(pageContext.getServletContext(), path) != null ? EVAL_BODY_INCLUDE : SKIP_BODY;
		} catch(MalformedURLException err) {
			throw new JspException(err);
		}
	}

	@Override
	public int doEndTag() {
		init();
		return EVAL_PAGE;
	}

	public String getPage() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
