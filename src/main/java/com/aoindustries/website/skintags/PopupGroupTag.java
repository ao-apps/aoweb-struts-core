/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import com.aoindustries.website.Skin;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Causes all nested popups to only display one at a time.
 *
 * @see  PopupTag
 *
 * @author  AO Industries, Inc.
 */
public class PopupGroupTag extends BodyTagSupport {

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE_NAME = PopupGroupTag.class.getName()+".sequence";

	private static final long serialVersionUID = 1L;

	long sequenceId;

	public PopupGroupTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		Sequence sequence = (Sequence)req.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME);
		if(sequence==null) req.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME, sequence = new UnsynchronizedSequence());
		sequenceId = sequence.getNextSequenceValue();
		Skin skin = SkinTag.getSkin(pageContext);
		skin.beginPopupGroup(req, pageContext.getOut(), sequenceId);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		Skin skin = SkinTag.getSkin(pageContext);
		skin.endPopupGroup((HttpServletRequest)pageContext.getRequest(), pageContext.getOut(), sequenceId);
		return EVAL_PAGE;
	}
}
