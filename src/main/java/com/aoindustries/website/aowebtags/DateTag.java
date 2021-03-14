/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2015, 2016, 2019, 2020, 2021  AO Industries, Inc.
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

import static com.aoindustries.encoding.JavaScriptInXhtmlEncoder.encodeJavaScriptInXhtml;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.html.servlet.Union_Palpable_Phrasing;
import com.aoindustries.servlet.jsp.tagext.JspTagUtils;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class DateTag extends BodyTagSupport {

	/**
	 * The request attribute name used to store the sequence.
	 */
	private static final String SEQUENCE_REQUEST_ATTRIBUTE = DateTag.class.getName() + ".sequence";

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  SQLUtility#formatDate(long)
	 */
	public static void writeDateJavaScript(long date, Sequence sequence, Union_Palpable_Phrasing<?> content, Appendable scriptOut) throws IOException {
		String dateString = SQLUtility.formatDate(date);
		long id = sequence.getNextSequenceValue();
		String idString = Long.toString(id);
		// Write the element
		content.span().id(idAttr -> idAttr.append("chainWriterDate").append(idString)).__(dateString);
		// Write the shared script only on first sequence
		if(id == 1) {
			scriptOut.append("  function chainWriterUpdateDate(id, millis, serverValue) {\n"
						   + "    if(document.getElementById) {\n"
						   + "      var date=new Date(millis);\n"
						   + "      var clientValue=date.getFullYear() + \"-\";\n"
						   + "      var month=date.getMonth()+1;\n"
						   + "      if(month<10) clientValue+=\"0\";\n"
						   + "      clientValue+=month+\"-\";\n"
						   + "      var day=date.getDate();\n"
						   + "      if(day<10) clientValue+=\"0\";\n"
						   + "      clientValue+=day;\n"
						   + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterDate\"+id).firstChild.nodeValue=clientValue;\n"
						   + "    }\n"
						   + "  }\n");
		}
		scriptOut.append("  chainWriterUpdateDate(");
		scriptOut.append(idString);
		scriptOut.append(", ");
		scriptOut.append(Long.toString(date));
		scriptOut.append(", \"");
		encodeJavaScriptInXhtml(dateString, scriptOut);
		scriptOut.append("\");\n");
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Prints nothing when the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  SQLUtility#formatDate(java.lang.Long)
	 */
	public static void writeDateJavaScript(Long date, Sequence sequence, Union_Palpable_Phrasing<?> content, Appendable scriptOut) throws IOException {
		if(date != null) writeDateJavaScript(date.longValue(), sequence, content, scriptOut);
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Prints nothing when the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  SQLUtility#formatDate(java.util.Date)
	 */
	public static void writeDateJavaScript(Date date, Sequence sequence, Union_Palpable_Phrasing<?> content, Appendable scriptOut) throws IOException {
		if(date != null) writeDateJavaScript(date.getTime(), sequence, content, scriptOut);
	}

	private static final long serialVersionUID = 1L;

	public DateTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			String millisString = getBodyContent().getString().trim();
			if(!millisString.isEmpty()) {
				Long date = Long.parseLong(millisString);
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				DocumentEE document = new DocumentEE(
					pageContext.getServletContext(),
					request,
					(HttpServletResponse)pageContext.getResponse(),
					pageContext.getOut(),
					false, // Do not add extra newlines to JSP
					false  // Do not add extra indentation to JSP
				);
				// Resolve the sequence
				Sequence sequence = (Sequence)request.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE);
				if(sequence == null) request.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE, sequence = new UnsynchronizedSequence());
				// Resolve the scriptOut
				Optional<ScriptGroupTag> scriptGroupTag = JspTagUtils.findAncestor(this, ScriptGroupTag.class);
				if(scriptGroupTag.isPresent()) {
					writeDateJavaScript(date, sequence, document, scriptGroupTag.get().getScriptOut());
				} else {
					CharArrayWriter scriptOut = new CharArrayWriter();
					writeDateJavaScript(date, sequence, document, scriptOut);
					try (MediaWriter script = document.script().out__()) {
						scriptOut.writeTo(script);
					}
				}
			}
			return EVAL_PAGE;
		} catch(IOException err) {
			throw new JspTagException(err);
		}
	}
}
