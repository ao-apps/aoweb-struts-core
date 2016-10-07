/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.aowebtags;

import com.aoindustries.util.Sequence;
import com.aoindustries.util.UnsynchronizedSequence;
import static com.aoindustries.website.ApplicationResources.accessor;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
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
	private static final String SEQUENCE_REQUEST_ATTRIBUTE_NAME = ScriptGroupTag.class.getName()+".sequence";

	private static final long serialVersionUID = 1L;

	private String onloadMode;

	final private StringBuilder scriptOut = new StringBuilder();

	public ScriptGroupTag() {
		init();
	}

	private void init() {
		onloadMode = "none";
		// Bring back down to size if exceeds MAX_PERSISTENT_BUFFER_SIZE
		if(scriptOut.length()>MAX_PERSISTENT_BUFFER_SIZE) {
			scriptOut.setLength(MAX_PERSISTENT_BUFFER_SIZE);
			scriptOut.trimToSize();
		}
		scriptOut.setLength(0);
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if(scriptOut.length()>0) {
				JspWriter out = pageContext.getOut();
				if("none".equals(onloadMode)) {
					out.print("<script type='text/javascript'>\n"
							+ "  // <![CDATA[\n");
					out.print(scriptOut);
					out.print("  // ]]>\n"
							+ "</script>\n");
				} else {
					ServletRequest request = pageContext.getRequest();
					Sequence sequence = (Sequence)request.getAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME);
					if(sequence==null) request.setAttribute(SEQUENCE_REQUEST_ATTRIBUTE_NAME, sequence = new UnsynchronizedSequence());
					long sequenceId = sequence.getNextSequenceValue();
					if("before".equals(onloadMode)) {
						out.print("<script type='text/javascript'>\n"
								+ "  // <![CDATA[\n"
								+ "  var scriptOutOldOnload"); out.print(sequenceId); out.print("=window.onload;\n"
								+ "  function scriptOutOnload"); out.print(sequenceId); out.print("() {\n");
						out.print(scriptOut);
						out.print("    if(scriptOutOldOnload"); out.print(sequenceId); out.print(") {\n"
								+ "      scriptOutOldOnload"); out.print(sequenceId); out.print("();\n"
								+ "      scriptOutOldOnload"); out.print(sequenceId); out.print("=null;\n"
								+ "    }\n"
								+ "  }\n"
								+ "  window.onload = scriptOutOnload"); out.print(sequenceId); out.print(";\n"
								+ "  // ]]>\n"
								+ "</script>\n");
					} else if("after".equals(onloadMode)) {
						out.print("<script type='text/javascript'>\n"
								+ "  // <![CDATA[\n"
								+ "  var scriptOutOldOnload"); out.print(sequenceId); out.print("=window.onload;\n"
								+ "  function scriptOutOnload"); out.print(sequenceId); out.print("() {\n"
								+ "    if(scriptOutOldOnload"); out.print(sequenceId); out.print(") {\n"
								+ "      scriptOutOldOnload"); out.print(sequenceId); out.print("();\n"
								+ "      scriptOutOldOnload"); out.print(sequenceId); out.print("=null;\n"
								+ "    }\n");
						out.print(scriptOut);
						out.print("  }\n"
								+ "  window.onload = scriptOutOnload"); out.print(sequenceId); out.print(";\n"
								+ "  // ]]>\n"
								+ "</script>\n");
					} else {
						throw new JspException(accessor.getMessage("aowebtags.ScriptGroupTag.onloadMode.invalid", onloadMode));
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
