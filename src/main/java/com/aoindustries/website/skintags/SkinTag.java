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

import com.aoindustries.website.Constants;
import com.aoindustries.website.Skin;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * Writes the skin header and footer.
 *
 * @author  AO Industries, Inc.
 */
public class SkinTag extends PageAttributesBodyTag {

	private static final long serialVersionUID = 1L;

	/**
	 * Gets the current skin from the session.  It is assumed the skin is already set.  Will throw an exception if not available.
	 */
	public static Skin getSkin(PageContext pageContext) throws JspException {
		Skin skin = (Skin)pageContext.getAttribute(Constants.SKIN, PageContext.REQUEST_SCOPE);
		if(skin==null) {
			HttpSession session = pageContext.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
			throw new JspException(applicationResources.getMessage(locale, "skintags.unableToFindSkinInRequest"));
		}
		return skin;
	}

	private String layout;
	private String onload;

	public SkinTag() {
		init();
	}

	private void init() {
		layout = "normal";
		onload = null;
	}

	@Override
	public int doStartTag(PageAttributes pageAttributes) throws JspException {
		pageAttributes.setLayout(layout);
		pageAttributes.setOnload(onload);

		Skin skin = SkinTag.getSkin(pageContext);

		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		skin.startSkin(req, resp, pageContext.getOut(), pageAttributes);

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag(PageAttributes pageAttributes) throws JspException {
		try {
			Skin skin = SkinTag.getSkin(pageContext);

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.endSkin(req, resp, pageContext.getOut(), pageAttributes);

			return EVAL_PAGE;
		} finally {
			init();
		}
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getOnload() {
		return onload;
	}

	public void setOnload(String onload) {
		this.onload = onload;
	}
}
