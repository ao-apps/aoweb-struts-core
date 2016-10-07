/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import com.aoindustries.website.Skin;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * Renders one popup, that may optionally be nested in a PopupGroupTag.
 *
 * @see  PopupGroupTag
 *
 * @author  AO Industries, Inc.
 */
public class PopupTag extends BodyTagSupport {

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE_NAME = PopupTag.class.getName()+".sequence";

	private static final long serialVersionUID = 1L;

	long sequenceId;
	String width;

	public PopupTag() {
		init();
	}

	private void init() {
		this.width = null;
	}

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		Sequence sequence = (Sequence)req.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME);
		if(sequence==null) req.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME, sequence = new UnsynchronizedSequence());
		sequenceId = sequence.getNextSequenceValue();
		Skin skin = SkinTag.getSkin(pageContext);
		// Look for containing popupGroup
		PopupGroupTag popupGroupTag = (PopupGroupTag)findAncestorWithClass(this, PopupGroupTag.class);
		if(popupGroupTag==null) {
			HttpSession session = pageContext.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = (MessageResources)req.getAttribute("/ApplicationResources");
			throw new JspException(applicationResources.getMessage(locale, "skintags.PopupTag.mustNestInPopupGroupTag"));
		} else {
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
			skin.beginPopup(req, resp, pageContext.getOut(), popupGroupTag.sequenceId, sequenceId, width);
		}
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException  {
		try {
			Skin skin = SkinTag.getSkin(pageContext);
			// Look for containing popupGroup
			PopupGroupTag popupGroupTag = (PopupGroupTag)findAncestorWithClass(this, PopupGroupTag.class);
			if(popupGroupTag==null) {
				HttpSession session = pageContext.getSession();
				Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
				MessageResources applicationResources = (MessageResources)pageContext.getRequest().getAttribute("/ApplicationResources");
				throw new JspException(applicationResources.getMessage(locale, "skintags.PopupTag.mustNestInPopupGroupTag"));
			} else {
				HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
				skin.endPopup((HttpServletRequest)pageContext.getRequest(), resp, pageContext.getOut(), popupGroupTag.sequenceId, sequenceId, width);
			}
			return EVAL_PAGE;
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
