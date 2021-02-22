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
import com.aoindustries.servlet.jsp.tagext.JspTagUtils;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Renders one popup, that may optionally be nested in a PopupGroupTag.
 *
 * @see  PopupGroupTag
 *
 * @author  AO Industries, Inc.
 */
public class PopupTag extends BodyTagSupport {

	public static final String TAG_NAME = "<skin:popup>";

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE = PopupTag.class.getName() + ".sequence";

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("PackageVisibleField")
	long sequenceId;
	private String width;

	public PopupTag() {
		init();
	}

	private void init() {
		this.width = null;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			Sequence sequence = (Sequence)req.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE);
			if(sequence==null) req.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE, sequence = new UnsynchronizedSequence());
			sequenceId = sequence.getNextSequenceValue();
			// Look for containing popupGroup
			PopupGroupTag popupGroupTag = JspTagUtils.requireAncestor(TAG_NAME, this, PopupGroupTag.TAG_NAME, PopupGroupTag.class);
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			SkinTag.getSkin(pageContext).beginPopup(
				req,
				resp,
				DocumentEE.get(pageContext.getServletContext(), req, resp, pageContext.getOut()),
				popupGroupTag.sequenceId,
				sequenceId,
				width
			);
			return EVAL_BODY_INCLUDE;
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public int doEndTag() throws JspException  {
		try {
			// Look for containing popupGroup
			PopupGroupTag popupGroupTag = JspTagUtils.requireAncestor(TAG_NAME, this, PopupGroupTag.TAG_NAME, PopupGroupTag.class);
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			SkinTag.getSkin(pageContext).endPopup(
				req,
				resp,
				DocumentEE.get(pageContext.getServletContext(), req, resp, pageContext.getOut()),
				popupGroupTag.sequenceId,
				sequenceId,
				width
			);
			return EVAL_PAGE;
		} catch(IOException e) {
			throw new JspTagException(e);
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
}
