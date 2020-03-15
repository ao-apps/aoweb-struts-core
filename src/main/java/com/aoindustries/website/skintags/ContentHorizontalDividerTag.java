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

import com.aoindustries.lang.Strings;
import com.aoindustries.website.Skin;
import java.util.List;
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
public class ContentHorizontalDividerTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private String colspansAndDirections;
	private boolean endsInternal;

	public ContentHorizontalDividerTag() {
		init();
	}

	private void init() {
		this.colspansAndDirections = "1";
		this.endsInternal = false;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			ContentTag contentTag = (ContentTag)findAncestorWithClass(this, ContentTag.class);
			if(contentTag==null) {
				HttpSession session = pageContext.getSession();
				Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
				MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
				throw new JspException(applicationResources.getMessage(locale, "skintags.ContentHorizontalDividerTag.mustNestInContentTag"));
			}

			Skin skin = SkinTag.getSkin(pageContext);

			List<String> list = Strings.splitStringCommaSpace(colspansAndDirections);
			if((list.size()&1)==0) {
				HttpSession session = pageContext.getSession();
				Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
				MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
				throw new JspException(applicationResources.getMessage(locale, "skintags.ContentHorizontalDivider.colspansAndDirections.mustBeOddNumberElements"));
			}
			int[] array = new int[list.size()];
			for(int c=0;c<list.size();c+=2) {
				if(c>0) {
					String direction = list.get(c-1);
					if("up".equalsIgnoreCase(direction)) array[c-1]=Skin.UP;
					else if("down".equalsIgnoreCase(direction)) array[c-1]=Skin.DOWN;
					else if("upAndDown".equalsIgnoreCase(direction)) array[c-1]=Skin.UP_AND_DOWN;
					else {
						HttpSession session = pageContext.getSession();
						Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
						MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
						throw new JspException(applicationResources.getMessage(locale, "skintags.ContentHorizontalDivider.colspansAndDirections.invalidDirection", direction));
					}
				}
				array[c]=Integer.parseInt(list.get(c));
			}

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.printContentHorizontalDivider(req, resp, pageContext.getOut(), array, endsInternal);

			return SKIP_BODY;
		} finally {
			init();
		}
	}

	public String getColspansAndDirections() {
		return colspansAndDirections;
	}

	public void setColspansAndDirections(String colspansAndDirections) {
		this.colspansAndDirections = colspansAndDirections;
	}

	public boolean isEndsInternal() {
		return endsInternal;
	}

	public void setEndsInternal(boolean endsInternal) {
		this.endsInternal = endsInternal;
	}
}
