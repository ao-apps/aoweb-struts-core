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

import com.aoindustries.encoding.Doctype;
import com.aoindustries.encoding.Serialization;
import com.aoindustries.encoding.servlet.DoctypeEE;
import com.aoindustries.encoding.servlet.SerializationEE;
import com.aoindustries.html.any.AnyDocument;
import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.lang.LocalizedIllegalArgumentException;
import com.aoindustries.servlet.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.taglib.HtmlTag;
import com.aoindustries.website.Constants;
import static com.aoindustries.website.Resources.PACKAGE_RESOURCES;
import com.aoindustries.website.Skin;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TryCatchFinally;
import org.apache.struts.Globals;

/**
 * Writes the skin header and footer.
 *
 * @author  AO Industries, Inc.
 */
public class SkinTag extends PageAttributesBodyTag implements TryCatchFinally {

	/**
	 * Gets the current skin from the session.  It is assumed the skin is already set.  Will throw an exception if not available.
	 */
	public static Skin getSkin(PageContext pageContext) throws JspException {
		Skin skin = (Skin)pageContext.getAttribute(Constants.SKIN, PageContext.REQUEST_SCOPE);
		if(skin==null) {
			throw new LocalizedJspTagException(PACKAGE_RESOURCES, "skintags.unableToFindSkinInRequest");
		}
		return skin;
	}

	public SkinTag() {
		init();
	}

	private static final long serialVersionUID = 2L;

	private Serialization serialization;
	public void setSerialization(String serialization) {
		if(serialization == null) {
			this.serialization = null;
		} else {
			serialization = serialization.trim();
			this.serialization = (serialization.isEmpty() || "auto".equalsIgnoreCase(serialization)) ? null : Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
		}
	}

	private Doctype doctype;
	public void setDoctype(String doctype) {
		if(doctype == null) {
			this.doctype = null;
		} else {
			doctype = doctype.trim();
			this.doctype = (doctype.isEmpty() || "default".equalsIgnoreCase(doctype)) ? null : Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
		}
	}

	private Boolean autonli;
	public void setAutonli(String autonli) {
		if(autonli == null) {
			this.autonli = null;
		} else {
			autonli = autonli.trim();
			if(autonli.isEmpty() || "auto".equalsIgnoreCase(autonli)) {
				this.autonli = null;
			} else if("true".equalsIgnoreCase(autonli)) {
				this.autonli = true;
			} else if("false".equalsIgnoreCase(autonli)) {
				this.autonli = false;
			} else {
				throw new LocalizedIllegalArgumentException(HtmlTag.RESOURCES, "autonli.invalid", autonli);
			}
		}
	}

	private Boolean indent;
	public void setIndent(String indent) {
		if(indent == null) {
			this.indent = null;
		} else {
			indent = indent.trim();
			if(indent.isEmpty() || "auto".equalsIgnoreCase(indent)) {
				this.indent = null;
			} else if("true".equalsIgnoreCase(indent)) {
				this.indent = true;
			} else if("false".equalsIgnoreCase(indent)) {
				this.indent = false;
			} else {
				throw new LocalizedIllegalArgumentException(HtmlTag.RESOURCES, "indent.invalid", indent);
			}
		}
	}

	private String layout;
	public void setLayout(String layout) {
		this.layout = layout.trim();
	}

	private String onload;
	public void setOnload(String onload) {
		this.onload = onload;
	}

	// Values that are used in doFinally
	private transient Serialization oldSerialization;
	private transient boolean setSerialization;
	private transient Doctype oldDoctype;
	private transient boolean setDoctype;
	private transient Boolean oldAutonli;
	private transient boolean setAutonli;
	private transient Boolean oldIndent;
	private transient boolean setIndent;

	private void init() {
		serialization = null;
		doctype = Doctype.DEFAULT;
		autonli = null;
		indent = null;
		layout = "normal";
		onload = null;
		oldSerialization = null;
		setSerialization = false;
		oldDoctype = null;
		setDoctype = false;
		oldAutonli = null;
		setAutonli = false;
		oldIndent = null;
		setIndent = false;
	}

	@Override
	public int doStartTag(PageAttributes pageAttributes) throws JspException, IOException {
		try {
			pageAttributes.setLayout(layout);
			pageAttributes.setOnload(onload);

			ServletContext servletContext = pageContext.getServletContext();
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

			if(serialization == null) {
				serialization = SerializationEE.get(servletContext, request);
				oldSerialization = null;
				setSerialization = false;
			} else {
				oldSerialization = SerializationEE.replace(request, serialization);
				setSerialization = true;
			}
			if(doctype == null) {
				doctype = DoctypeEE.get(servletContext, request);
				oldDoctype = null;
				setDoctype = false;
			} else {
				oldDoctype = DoctypeEE.replace(request, doctype);
				setDoctype = true;
			}
			if(autonli == null) {
				autonli = DocumentEE.getAutonli(servletContext, request);
				oldAutonli = null;
				setAutonli = false;
			} else {
				oldAutonli = DocumentEE.replaceAutonli(request, autonli);
				setAutonli = true;
			}
			assert autonli != null;
			if(indent == null) {
				indent = DocumentEE.getIndent(servletContext, request);
				oldIndent = null;
				setIndent = false;
			} else {
				oldIndent = DocumentEE.replaceIndent(request, indent);
				setIndent = true;
			}
			assert indent != null;

			// Clear the output buffer
			response.resetBuffer();

			// Set the content type
			ServletUtil.setContentType(response, serialization.getContentType(), AnyDocument.ENCODING.name());

			// Set the response locale from the Struts locale
			Locale locale = (Locale)pageContext.getSession().getAttribute(Globals.LOCALE_KEY);
			response.setLocale(locale);

			// Set the Struts XHTML mode by Serialization
			pageContext.setAttribute(
				Globals.XHTML_KEY,
				Boolean.toString(serialization == Serialization.XML),
	            PageContext.PAGE_SCOPE
			);

			// Start the skin
			SkinTag.getSkin(pageContext).startSkin(
				request,
				response,
				new DocumentEE(servletContext, request, response, pageContext.getOut(), autonli, indent),
				pageAttributes
			);

			return EVAL_BODY_INCLUDE;
		} catch(ServletException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public int doEndTag(PageAttributes pageAttributes) throws JspException, IOException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		SkinTag.getSkin(pageContext).endSkin(
			request,
			response,
			new DocumentEE(pageContext.getServletContext(), request, response, pageContext.getOut(), autonli, indent),
			pageAttributes
		);
		return EVAL_PAGE;
	}

	@Override
	public void doCatch(Throwable t) throws Throwable {
		throw t;
	}

	@Override
	public void doFinally() {
		try {
			ServletRequest request = pageContext.getRequest();
			if(setIndent) DocumentEE.setIndent(request, oldIndent);
			if(setAutonli) DocumentEE.setIndent(request, oldAutonli);
			if(setDoctype) DoctypeEE.set(request, oldDoctype);
			if(setSerialization) SerializationEE.set(request, oldSerialization);
		} finally {
			init();
		}
	}
}
