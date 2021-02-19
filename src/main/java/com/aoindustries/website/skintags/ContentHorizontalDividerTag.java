/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.lang.Strings;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.servlet.jsp.tagext.JspTagUtils;
import static com.aoindustries.website.Resources.PACKAGE_RESOURCES;
import com.aoindustries.website.Skin;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class ContentHorizontalDividerTag extends TagSupport {

	public static final String TAG_NAME = "<skin:contentHorizontalDivider>";

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
			JspTagUtils.requireAncestor(TAG_NAME, this, ContentTag.TAG_NAME, ContentTag.class);

			List<String> list = Strings.splitCommaSpace(colspansAndDirections);
			if((list.size()&1)==0) {
				throw new LocalizedJspTagException(PACKAGE_RESOURCES, "skintags.ContentHorizontalDivider.colspansAndDirections.mustBeOddNumberElements");
			}
			int[] array = new int[list.size()];
			for(int c=0;c<list.size();c+=2) {
				if(c>0) {
					String direction = list.get(c-1);
					if("up".equalsIgnoreCase(direction)) array[c-1]=Skin.UP;
					else if("down".equalsIgnoreCase(direction)) array[c-1]=Skin.DOWN;
					else if("upAndDown".equalsIgnoreCase(direction)) array[c-1]=Skin.UP_AND_DOWN;
					else {
						throw new LocalizedJspTagException(PACKAGE_RESOURCES, "skintags.ContentHorizontalDivider.colspansAndDirections.invalidDirection", direction);
					}
				}
				array[c]=Integer.parseInt(list.get(c));
			}

			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			SkinTag.getSkin(pageContext).printContentHorizontalDivider(req,
				resp,
				DocumentEE.get(pageContext.getServletContext(), req, resp, pageContext.getOut()),
				array,
				endsInternal
			);
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
