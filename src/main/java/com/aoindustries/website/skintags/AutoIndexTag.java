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
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class AutoIndexTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	public AutoIndexTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		Skin skin = SkinTag.getSkin(pageContext);
		PageAttributes pageAttributes = PageAttributesBodyTag.getPageAttributes(pageContext);
		skin.printAutoIndex((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), pageContext.getOut(), pageAttributes);
		return SKIP_BODY;
	}
}
