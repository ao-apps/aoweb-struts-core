/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009-2013, 2015, 2016, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.html.Html;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import java.io.CharArrayWriter;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class TimeTag extends BodyTagSupport {

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE_NAME = TimeTag.class.getName() + ".sequence";

	private static final long serialVersionUID = 1L;

	public TimeTag() {
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			String millisString = getBodyContent().getString().trim();
			if(!millisString.isEmpty()) {
				Long time = Long.parseLong(millisString);
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				JspWriter out = pageContext.getOut();
				// Resolve the sequence
				Sequence sequence = (Sequence)request.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME);
				if(sequence == null) request.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME, sequence = new UnsynchronizedSequence());
				// Resolve the scriptOut
				ScriptGroupTag scriptGroupTag = (ScriptGroupTag)findAncestorWithClass(this, ScriptGroupTag.class);
				if(scriptGroupTag != null) {
					ChainWriter.writeTimeJavaScript(time, sequence, out, scriptGroupTag.getScriptOut());
				} else {
					CharArrayWriter scriptOut = new CharArrayWriter();
					ChainWriter.writeTimeJavaScript(time, sequence, out, scriptOut);
					Html html = HtmlEE.get(pageContext.getServletContext(), request, out);
					try (MediaWriter script = html.script().out__()) {
						scriptOut.writeTo(script);
					}
				}
			}
			return EVAL_PAGE;
		} catch(IOException err) {
			throw new JspException(err);
		}
	}
}
