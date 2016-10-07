/*
 * Copyright 2007-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import com.aoindustries.util.StringUtility;
import com.aoindustries.website.Skin;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.Globals;

/**
 * Sets the content type for the page.
 *
 * @see  Skin#getCharacterSet(Locale)
 *
 * @author  AO Industries, Inc.
 */
public class SetContentTypeTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	public SetContentTypeTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			// Skin skin = SkinTag.getSkin(pageContext);

			ServletResponse response = pageContext.getResponse();

			boolean allowApplicationXhtmlXml = "true".equals(pageContext.getServletContext().getInitParameter("com.aoindustries.website.skintags.SetContentTypeTag.allowApplicationXhtmlXml"));
			boolean isApplicationXhtmlXml;
			if(!allowApplicationXhtmlXml) {
				isApplicationXhtmlXml = false;
			} else {
				// Determine the content type by the rules defined in http://www.w3.org/TR/xhtml-media-types/
				// Default to application/xhtml+xml as discussed at http://www.smackthemouse.com/xhtmlxml
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				Enumeration<String> acceptValues = request.getHeaders("Accept");
				boolean hasAcceptHeader = false;
				boolean hasAcceptApplicationXhtmlXml = false;
				boolean hasAcceptHtmlHtml = false;
				boolean hasAcceptStarStar = false;
				if(acceptValues!=null) {
					while(acceptValues.hasMoreElements()) {
						hasAcceptHeader = true;
						for(String value : StringUtility.splitString(acceptValues.nextElement(), ',')) {
							value = value.trim();
							final List<String> params = StringUtility.splitString(value, ';');
							final int paramsLen = params.size();
							if(paramsLen>0) {
								String acceptType = params.get(0).trim();
								if(acceptType.equals("*/*")) {
									// No q parameter parsing for */*
									hasAcceptStarStar = true;
								} else if(
									// Parse and check the q for these two types
									acceptType.equalsIgnoreCase("application/xhtml+xml")
									|| acceptType.equalsIgnoreCase("text/html")
								) {
									// Find any q value
									boolean hasNegativeQ = false;
									for(int paramNum = 1; paramNum<paramsLen; paramNum++) {
										String paramSet = params.get(paramNum).trim();
										if(paramSet.startsWith("q=") || paramSet.startsWith("Q=")) {
											try {
												float q = Float.parseFloat(paramSet.substring(2).trim());
												if(q<0) {
													hasNegativeQ = true;
													break;
												}
											} catch(NumberFormatException err) {
												// Intentionally ignored
											}
										}
									}
									if(!hasNegativeQ) {
										if(acceptType.equalsIgnoreCase("application/xhtml+xml")) hasAcceptApplicationXhtmlXml = true;
										else if(acceptType.equalsIgnoreCase("text/html")) hasAcceptHtmlHtml = true;
										else throw new AssertionError("Unexpected value for acceptType: "+acceptType);
									}
								}
							}
						}
					}
				}
				// If the Accept header explicitly contains application/xhtml+xml  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
				if(hasAcceptApplicationXhtmlXml) isApplicationXhtmlXml = true;
				// If the Accept header explicitly contains text/html  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
				else if(hasAcceptHtmlHtml) isApplicationXhtmlXml = false;
				// If the accept header contains "*/*" (a convention some user agents use to indicate that they will accept anything), deliver the document using text/html.
				else if(hasAcceptStarStar) isApplicationXhtmlXml = false;
				// If has no accept headers
				else if(!hasAcceptHeader) isApplicationXhtmlXml = true;
				// This choice is not clear from either of the cited documents.  If there is an accept line,
				// and it doesn't have */* or application/xhtml+xml or text/html, we'll serve as text/html
				// since it is a fairly broken client anyway and would be even less likely to know xhtml.
				else isApplicationXhtmlXml = false;
			}

			// Firefox: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
			// IE 6: */*
			// IE 8: */*
			// IE 8 Compat: */*
			String contentType;
			if(isApplicationXhtmlXml) {
				contentType = "application/xhtml+xml";
			} else {
				contentType = "text/html";
			}

			// Set the content type
			// System.out.println("DEBUG: SetContentTypeTag: doStartTag: response.getContentType()="+response.getContentType());
			response.setContentType(contentType);
			// System.out.println("DEBUG: SetContentTypeTag: doStartTag: response.setContentType("+contentType+")");
			// System.out.println("DEBUG: SetContentTypeTag: doStartTag: response.getContentType()="+response.getContentType());

			// Set the locale
			Locale locale = (Locale)pageContext.getSession().getAttribute(Globals.LOCALE_KEY);
			response.setLocale(locale);

			// Set the encoding
			String charset = Skin.getCharacterSet(locale);
			// System.out.println("DEBUG: SetContentTypeTag: doStartTag: response.getCharacterEncoding()="+response.getCharacterEncoding());
			response.setCharacterEncoding(charset);
			// System.out.println("DEBUG: SetContentTypeTag: doStartTag: response.setCharacterEncoding("+charset+")");
			// System.out.println("DEBUG: SetContentTypeTag: doStartTag: response.getCharacterEncoding()="+response.getCharacterEncoding());

			if(isApplicationXhtmlXml) {
				// Also write the xml declaration
				JspWriter out = pageContext.getOut();
				out.clear();
				response.resetBuffer(); // Cannot have even whitespace before the xml declaration
				out.print("<?xml version=\"1.0\" encoding=\"");
				out.print(charset);
				out.print("\"?>");
			}

			return SKIP_BODY;
		} catch(IOException err) {
			throw new JspException(err);
		}
	}
}
