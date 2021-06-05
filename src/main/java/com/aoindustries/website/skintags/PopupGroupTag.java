/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.lang.util.Sequence;
import com.aoapps.lang.util.UnsynchronizedSequence;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Causes all nested popups to only display one at a time.
 *
 * @see  PopupTag
 *
 * @author  AO Industries, Inc.
 */
public class PopupGroupTag extends BodyTagSupport {

	public static final String TAG_NAME = "<skin:popupGroup>";

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE = PopupGroupTag.class.getName()+".sequence";

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("PackageVisibleField")
	long sequenceId;

	public PopupGroupTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			Sequence sequence = (Sequence)req.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE);
			if(sequence==null) req.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE, sequence = new UnsynchronizedSequence());
			sequenceId = sequence.getNextSequenceValue();
			SkinTag.getSkin(pageContext).beginPopupGroup(
				req,
				resp,
				new DocumentEE(
					pageContext.getServletContext(),
					req,
					resp,
					pageContext.getOut(),
					false, // Do not add extra newlines to JSP
					false  // Do not add extra indentation to JSP
				),
				sequenceId
			);
			return EVAL_BODY_INCLUDE;
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			SkinTag.getSkin(pageContext).endPopupGroup(
				req,
				resp,
				new DocumentEE(
					pageContext.getServletContext(),
					req,
					resp,
					pageContext.getOut(),
					false, // Do not add extra newlines to JSP
					false  // Do not add extra indentation to JSP
				),
				sequenceId
			);
			return EVAL_PAGE;
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}
}
