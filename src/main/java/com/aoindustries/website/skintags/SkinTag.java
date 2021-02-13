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
import com.aoindustries.html.Html;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.servlet.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.website.Constants;
import static com.aoindustries.website.Resources.PACKAGE_RESOURCES;
import com.aoindustries.website.Skin;
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

	private static final long serialVersionUID = 1L;

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

	private Serialization serialization;
	private Doctype doctype;
	private String layout;
	private String onload;

	public SkinTag() {
		init();
	}

	private void init() {
		serialization = null;
		doctype = Doctype.DEFAULT;
		layout = "normal";
		onload = null;
	}

	private Serialization oldSerialization;
	private boolean setSerialization;
	private Doctype oldDoctype;
	private boolean setDoctype;

	@Override
	public int doStartTag(PageAttributes pageAttributes) throws JspException {
		try {
			pageAttributes.setLayout(layout);
			pageAttributes.setOnload(onload);

			ServletContext servletContext = pageContext.getServletContext();
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();

			if(serialization == null) {
				serialization = SerializationEE.get(servletContext, req);
				oldSerialization = null;
				setSerialization = false;
			} else {
				oldSerialization = SerializationEE.replace(req, serialization);
				setSerialization = true;
			}
			if(doctype == null) {
				doctype = DoctypeEE.get(servletContext, req);
				oldDoctype = null;
				setDoctype = false;
			} else {
				oldDoctype = DoctypeEE.replace(req, doctype);
				setDoctype = true;
			}

			// Clear the output buffer
			resp.resetBuffer();

			// Set the content type
			ServletUtil.setContentType(resp, serialization.getContentType(), Html.ENCODING.name());

			// Set the response locale from the Struts locale
			Locale locale = (Locale)pageContext.getSession().getAttribute(Globals.LOCALE_KEY);
			resp.setLocale(locale);

			// Set the Struts XHTML mode by Serialization
			pageContext.setAttribute(
				Globals.XHTML_KEY,
				(serialization == Serialization.XML) ? "true" : "false", // TODO: Boolean.toString
	            PageContext.PAGE_SCOPE
			);

			// Start the skin
			SkinTag.getSkin(pageContext).startSkin(
				req,
				resp,
				HtmlEE.get(servletContext, req, resp, pageContext.getOut()),
				pageAttributes
			);

			return EVAL_BODY_INCLUDE;
		} catch(ServletException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public int doEndTag(PageAttributes pageAttributes) throws JspException {
		HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse resp = (HttpServletResponse)pageContext.getResponse();
		SkinTag.getSkin(pageContext).endSkin(
			req,
			resp,
			HtmlEE.get(pageContext.getServletContext(), req, resp, pageContext.getOut()),
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
			ServletRequest req = pageContext.getRequest();
			if(setDoctype) DoctypeEE.set(req, oldDoctype);
			if(setSerialization) SerializationEE.set(req, oldSerialization);
		} finally {
			init();
		}
	}

	public String getSerialization() {
		return (serialization == null) ? null : serialization.name();
	}

	public void setSerialization(String serialization) {
		if(serialization == null) {
			this.serialization = null;
		} else {
			serialization = serialization.trim();
			this.serialization = (serialization.isEmpty() || "auto".equalsIgnoreCase(serialization)) ? null : Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
		}
	}

	public String getDoctype() {
		return (doctype == null) ? null : doctype.name();
	}

	public void setDoctype(String doctype) {
		if(doctype == null) {
			this.doctype = null;
		} else {
			doctype = doctype.trim();
			this.doctype = (doctype.isEmpty() || "default".equalsIgnoreCase(doctype)) ? null : Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
		}
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout.trim();
	}

	public String getOnload() {
		return onload;
	}

	public void setOnload(String onload) {
		this.onload = onload;
	}
}
