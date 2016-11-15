/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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
package com.aoindustries.website.aowebtags;

import com.aoindustries.servlet.ServletContextCache;
import java.net.MalformedURLException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Evaluates the body if the provided page does not exist.
 *
 * @author  AO Industries, Inc.
 */
public class NotExistsTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	private String path;

	public NotExistsTag() {
		init();
	}

	private void init() {
		path = null;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			return ServletContextCache.getResource(pageContext.getServletContext(), path) != null ? SKIP_BODY : EVAL_BODY_INCLUDE;
		} catch(MalformedURLException err) {
			throw new JspException(err);
		}
	}

	@Override
	public int doEndTag() {
		init();
		return EVAL_PAGE;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
