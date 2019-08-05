/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2015, 2016, 2019  AO Industries, Inc.
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
import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class DateTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE_NAME = DateTag.class.getName()+".sequence";

	public DateTag() {
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			String millisString = getBodyContent().getString().trim();
			Long date;
			if(millisString.isEmpty()) date = null;
			else date = Long.parseLong(millisString);
			// Resolve the sequence
			ServletRequest request = pageContext.getRequest();
			Sequence sequence = (Sequence)request.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME);
			if(sequence==null) request.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME, sequence = new UnsynchronizedSequence());
			// Resolve the scriptOut
			ScriptGroupTag scriptGroupTag = (ScriptGroupTag)findAncestorWithClass(this, ScriptGroupTag.class);
			if(scriptGroupTag!=null) {
				ChainWriter.writeDateJavaScript(date, sequence, pageContext.getOut(), scriptGroupTag.getScriptOut());
			} else {
				StringBuilder scriptOut = new StringBuilder();
				ChainWriter.writeDateJavaScript(date, sequence, pageContext.getOut(), scriptOut);
				if(scriptOut.length() > 0) {
					JspWriter out = pageContext.getOut();
					out.print("<script type='text/javascript'>\n"
							+ "  // <![CDATA[\n");
					out.print(scriptOut);
					out.print("  // ]]>\n"
							+ "</script>\n");
				}
			}
			return EVAL_PAGE;
		} catch(IOException err) {
			throw new JspException(err);
		}
	}
}
