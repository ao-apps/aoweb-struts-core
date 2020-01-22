/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.html.Html;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import static com.aoindustries.website.ApplicationResources.accessor;
import java.io.CharArrayWriter;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptGroupTag extends BodyTagSupport {

	/**
	 * The maximum buffer size that will be allowed between requests.  This is
	 * so that an unusually large request will not continue to use lots of heap
	 * space.
	 */
	private static final int MAX_PERSISTENT_BUFFER_SIZE = 1024 * 1024;

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE_NAME = ScriptGroupTag.class.getName() + ".sequence";

	private static final long serialVersionUID = 1L;

	private String onloadMode;

	private CharArrayWriter scriptOut = new CharArrayWriter();

	public ScriptGroupTag() {
		init();
	}

	private void init() {
		onloadMode = "none";
		// Bring back down to size if exceeds MAX_PERSISTENT_BUFFER_SIZE
		if(scriptOut.size() > MAX_PERSISTENT_BUFFER_SIZE) {
			scriptOut = new CharArrayWriter();
		} else {
			scriptOut.reset();
		}
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if(scriptOut.size() > 0) {
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				Html html = HtmlEE.get(pageContext.getServletContext(), request, pageContext.getOut());
				try (MediaWriter script = html.script().out__()) {
					if("none".equals(onloadMode)) {
						scriptOut.writeTo(script);
					} else {
						Sequence sequence = (Sequence)request.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME);
						if(sequence == null) request.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME, sequence = new UnsynchronizedSequence());
						String sequenceId = Long.toString(sequence.getNextSequenceValue());
						boolean wroteScript = false;
						script.write("  var scriptOutOldOnload"); script.write(sequenceId); script.write("=window.onload;\n"
								+ "  function scriptOutOnload"); script.write(sequenceId); script.write("() {\n");
						if("before".equals(onloadMode)) {
							scriptOut.writeTo(script);
							wroteScript = true;
						}
						script.write("    if(scriptOutOldOnload"); script.write(sequenceId); script.write(") {\n"
								+ "      scriptOutOldOnload"); script.write(sequenceId); script.write("();\n"
								+ "      scriptOutOldOnload"); script.write(sequenceId); script.write("=null;\n"
								+ "    }\n");
						if(!wroteScript && "after".equals(onloadMode)) {
							scriptOut.writeTo(script);
							wroteScript = true;
						}
						script.write("  }\n"
								+ "  window.onload = scriptOutOnload"); script.write(sequenceId); script.write(";\n");
						if(!wroteScript) {
							throw new JspException(accessor.getMessage("aowebtags.ScriptGroupTag.onloadMode.invalid", onloadMode));
						}
					}
				}
			}
			return EVAL_PAGE;
		} catch(IOException err) {
			throw new JspException(err);
		} finally {
			init();
		}
	}

	public String getOnloadMode() {
		return onloadMode;
	}

	public void setOnloadMode(String onloadMode) {
		this.onloadMode = onloadMode;
	}

	/**
	 * Gets the buffered used to store the JavaScript.
	 */
	Appendable getScriptOut() {
		return scriptOut;
	}
}
