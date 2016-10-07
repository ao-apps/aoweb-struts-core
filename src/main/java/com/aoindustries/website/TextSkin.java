/*
 * Copyright 2007-2013, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Brand;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.encoding.NewEncodingUtils;
import com.aoindustries.encoding.TextInJavaScriptEncoder;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.encoding.TextInXhtmlEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.encodeTextInXhtml;
import com.aoindustries.net.UrlUtils;
import com.aoindustries.util.i18n.EditableResourceBundle;
import com.aoindustries.website.skintags.Child;
import com.aoindustries.website.skintags.Meta;
import com.aoindustries.website.skintags.PageAttributes;
import com.aoindustries.website.skintags.Parent;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;

/**
 * The skin for the home page of the site.
 *
 * @author  AO Industries, Inc.
 */
public class TextSkin extends Skin {

	/**
	 * Reuse a single instance, not synchronized because if more than one is
	 * made no big deal.
	 */
	private static TextSkin instance;
	public static TextSkin getInstance() {
		if(instance==null) instance = new TextSkin();
		return instance;
	}

	protected TextSkin() {}

	@Override
	public String getName() {
		return "Text";
	}

	@Override
	public String getDisplay(HttpServletRequest req) throws JspException {
		HttpSession session = req.getSession();
		Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
		MessageResources applicationResources = (MessageResources)req.getAttribute("/ApplicationResources");
		if(applicationResources==null) throw new JspException("Unable to load resources: /ApplicationResources");
		return applicationResources.getMessage(locale, "TextSkin.name");
	}

	/**
	 * Print the logo for the top left part of the page.
	 */
	public void printLogo(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String urlBase) throws JspException {
		// Print no logo by default
	}

	/**
	 * Prints the search form, if any exists.
	 */
	public void printSearch(HttpServletRequest req, HttpServletResponse resp, JspWriter out) throws JspException {
	}

	/**
	 * Prints the common pages area, which is at the top of the site.
	 */
	public void printCommonPages(HttpServletRequest req, HttpServletResponse resp, JspWriter out) throws JspException {
	}

	/**
	 * Prints the lines to include any CSS files.
	 */
	public void printCssIncludes(HttpServletResponse resp, JspWriter out, String urlBase) throws JspException {
	}

	/**
	 * Prints the lines for any JavaScript sources.
	 */
	public void printJavaScriptSources(HttpServletResponse resp, JspWriter out, String urlBase) throws JspException {
	}

	/**
	 * Prints the line for the favicon.
	 */
	public void printFavIcon(HttpServletResponse resp, JspWriter out, String urlBase) throws JspException {
	}

	public static MessageResources getMessageResources(HttpServletRequest req) throws JspException {
		MessageResources resources = (MessageResources)req.getAttribute("/ApplicationResources");
		if(resources==null) throw new JspException("Unable to load resources: /ApplicationResources");
		return resources;
	}

	@Override
	public void startSkin(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes) throws JspException {
		try {
			String layout = pageAttributes.getLayout();
			if(!layout.equals(PageAttributes.LAYOUT_NORMAL)) throw new JspException("TODO: Implement layout: "+layout);
			HttpSession session = req.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = getMessageResources(req);
			String urlBase = getUrlBase(req);
			String path = pageAttributes.getPath();
			if(path.startsWith("/")) path=path.substring(1);
			final String fullPath = urlBase + path;
			final String encodedFullPath = resp.encodeURL(UrlUtils.encodeUrlPath(fullPath, resp.getCharacterEncoding()));
			ServletContext servletContext = session.getServletContext();
			SiteSettings settings = SiteSettings.getInstance(servletContext);
			List<Skin> skins = settings.getSkins();
			boolean isOkResponseStatus;
			{
				Integer responseStatus = (Integer)req.getAttribute(Constants.HTTP_SERVLET_RESPONSE_STATUS);
				isOkResponseStatus = responseStatus==null || responseStatus==HttpServletResponse.SC_OK;
			}

			out.print("  <head>\n");
			// If this is not the default skin, then robots noindex
			boolean robotsMetaUsed = false;
			if(!isOkResponseStatus || !getName().equals(skins.get(0).getName())) {
				out.print("    <meta name=\"ROBOTS\" content=\"NOINDEX, NOFOLLOW\" />\n");
				robotsMetaUsed = true;
			}
			// Default style language
			out.print("    <meta http-equiv=\"Content-Style-Type\" content=\"text/css\" />\n"
					+ "    <meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n");
			// If this is an authenticated page, redirect to session timeout after one hour
			AOServConnector aoConn = AuthenticatedAction.getAoConn(req, resp);
			if(isOkResponseStatus && aoConn!=null) {
				out.print("    <meta http-equiv=\"Refresh\" content=\"");
				encodeTextInXhtmlAttribute(Integer.toString(Math.max(60, session.getMaxInactiveInterval()-60)), out);
				encodeTextInXhtmlAttribute(";URL=", out);
				encodeTextInXhtmlAttribute(
					resp.encodeRedirectURL(
						urlBase
							+ "session-timeout.do?target=")
							+ URLEncoder.encode(fullPath, resp.getCharacterEncoding()),
						out
				);
				out.print("\" />\n");
			}
			for(Meta meta : pageAttributes.getMetas()) {
				// Skip ROBOTS if not on default skin
				boolean isRobots = meta.getName().equalsIgnoreCase("ROBOTS");
				if(!robotsMetaUsed || !isRobots) {
					out.print("    <meta");
					if(meta.getName() != null) {
						out.print(" name=\"");
						encodeTextInXhtmlAttribute(meta.getName(), out);
						out.write('"');
					}
					if(meta.getContent() != null) {
						out.print(" content=\"");
						encodeTextInXhtmlAttribute(meta.getContent(), out);
						out.write('"');
					}
					out.print(" />\n");
					if(isRobots) robotsMetaUsed = true;
				}
			}
			out.print("    <title>");
			List<Parent> parents = pageAttributes.getParents();
			for(Parent parent : parents) {
				encodeTextInXhtml(parent.getTitle(), out);
				out.print(" - ");
			}
			encodeTextInXhtml(pageAttributes.getTitle(), out);
			out.print("</title>\n"
					+ "    <meta http-equiv='Content-Type' content='");
			out.print(resp.getContentType());
			out.print("' />\n");
			Brand brand = settings.getBrand();
			if(isOkResponseStatus) {
				String googleVerify = brand.getAowebStrutsGoogleVerifyContent();
				if(googleVerify!=null) {
					out.print("    <meta name=\"verify-v1\" content=\""); encodeTextInXhtmlAttribute(googleVerify, out); out.print("\" />\n");
				}
			}
			String keywords = pageAttributes.getKeywords();
			if(keywords!=null && keywords.length()>0) {
				out.print("    <meta name='keywords' content='"); encodeTextInXhtmlAttribute(keywords, out); out.print("' />\n");
			}
			String description = pageAttributes.getDescription();
			if(description!=null && description.length()>0) {
				out.print("    <meta name='description' content='"); encodeTextInXhtmlAttribute(description, out); out.print("' />\n"
						+ "    <meta name='abstract' content='"); encodeTextInXhtmlAttribute(description, out); out.print("' />\n");
			}
			String copyright = pageAttributes.getCopyright();
			if(copyright!=null && copyright.length()>0) {
				out.print("    <meta name='copyright' content='"); encodeTextInXhtmlAttribute(copyright, out); out.print("' />\n");
			}
			String author = pageAttributes.getAuthor();
			if(author!=null && author.length()>0) {
				out.print("    <meta name='author' content='"); encodeTextInXhtmlAttribute(author, out); out.print("' />\n");
			}
			List<Language> languages = settings.getLanguages(req);
			printAlternativeLinks(resp, out, fullPath, languages);
			out.print("    <link rel='stylesheet' href='");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase+"textskin/global.css"),
				out
			);
			out.print("' type='text/css' />\n"
					+ "    <!--[if IE 6]>\n"
					+ "      <link rel='stylesheet' href='");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase+"textskin/global-ie6.css"),
				out
			);
			out.print("' type='text/css' />\n"
					+ "    <![endif]-->\n");
			printCssIncludes(resp, out, urlBase);
			defaultPrintLinks(out, pageAttributes, resp.getCharacterEncoding());
			printJavaScriptSources(resp, out, urlBase);
			out.print("    <script type='text/javascript' src='");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "commons-validator-1.3.1-compress.js"),
				out
			);
			out.print("'></script>\n");
			String googleAnalyticsNewTrackingCode = brand.getAowebStrutsGoogleAnalyticsNewTrackingCode();
			if(googleAnalyticsNewTrackingCode!=null) {
				out.print("    <script type='text/javascript' src='");
				out.print(req.isSecure() ? "https://ssl.google-analytics.com/ga.js" : "http://www.google-analytics.com/ga.js");
				out.print("'></script>\n");
			}
			printFavIcon(resp, out, urlBase);
			out.print("  </head>\n"
					+ "  <body");
			String onload = pageAttributes.getOnload();
			if(onload!=null && onload.length()>0) {
				out.print(" onload=\""); out.print(onload); out.print('"');
			}
			out.print(">\n"
					+ "    <table cellspacing='10' cellpadding='0'>\n"
					+ "      <tr>\n"
					+ "        <td valign='top'>\n");
			printLogo(req, resp, out, urlBase);
			if(aoConn!=null) {
				out.print("          <hr />\n"
						+ "          ");
				out.print(applicationResources.getMessage(locale, "TextSkin.logoutPrompt"));
				out.print("<form style='display:inline;' id='logout_form' method='post' action='");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(urlBase+"logout.do"),
					out
				);
				out.print("'><div style='display:inline;'><input type='hidden' name='target' value='");
				encodeTextInXhtmlAttribute(fullPath, out);
				out.print("' /><input type='submit' value='");
				encodeTextInXhtmlAttribute(applicationResources.getMessage(locale, "TextSkin.logoutButtonLabel"), out);
				out.print("' /></div></form>\n");
			} else {
				out.print("          <hr />\n"
						+ "          ");
				out.print(applicationResources.getMessage(locale, "TextSkin.loginPrompt"));
				out.print("<form style='display:inline;' id='login_form' method='post' action='");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(urlBase+"login.do"),
					out
				);
				out.print("'><div style='display:inline;'>");
				// Only include the target when they are not in the /clientarea/ part of the site
				if(path.startsWith("clientarea/")) {
					out.print("<input type='hidden' name='target' value='");
					encodeTextInXhtmlAttribute(fullPath, out);
					out.print("' />");
				}
				out.print("<input type='submit' value='");
				encodeTextInXhtmlAttribute(applicationResources.getMessage(locale, "TextSkin.loginButtonLabel"), out);
				out.print("' /></div></form>\n");
			}
			out.print("          <hr />\n"
					+ "          <div style='white-space: nowrap'>\n");
			if(skins.size()>1) {
				out.print("<script type='text/javascript'>\n"
						+ "  function selectLayout(layout) {\n");
				for(Skin skin : skins) {
					out.print("    if(layout=='");
					NewEncodingUtils.encodeTextInJavaScriptInXhtml(skin.getName(), out);
					out.print("') window.top.location.href='");
					NewEncodingUtils.encodeTextInJavaScriptInXhtml(
						resp.encodeURL(fullPath+(fullPath.indexOf('?')==-1 ? '?' : '&')+"layout="+skin.getName()),
						out
					);
					out.print("';\n");
				}
				out.print("  }\n"
						+ "</script>\n"
						+ "            <form action='' style='display:inline;'><div style='display:inline;'>\n"
						+ "              ");
				out.print(applicationResources.getMessage(locale, "TextSkin.layoutPrompt"));
				out.print("<select name='layout_selector' onchange='selectLayout(this.form.layout_selector.options[this.form.layout_selector.selectedIndex].value);'>\n");
				for(Skin skin : skins) {
					out.print("                <option value='");
					encodeTextInXhtmlAttribute(skin.getName(), out);
					out.print('\'');
					if(getName().equals(skin.getName())) out.print(" selected='selected'");
					out.print('>');
					encodeTextInXhtml(skin.getDisplay(req), out);
					out.print("</option>\n");
				}
				out.print("              </select>\n"
						+ "            </div></form><br />\n");
			}
			if(languages.size()>1) {
				out.print("            ");
				for(Language language : languages) {
					String url = language.getUrl();
					if(language.getCode().equalsIgnoreCase(locale.getLanguage())) {
						out.print("&#160;<a href='");
						encodeTextInXhtmlAttribute(
							resp.encodeURL(
								UrlUtils.encodeUrlPath(
									url==null
									? (fullPath+(fullPath.indexOf('?')==-1 ? "?" : "&")+"language="+language.getCode())
									: url,
									resp.getCharacterEncoding()
								)
							),
							out
						);
						out.print("' hreflang='");
						encodeTextInXhtmlAttribute(
							language.getCode(),
							out
						);
						out.print("'><img src='");
						encodeTextInXhtmlAttribute(
							resp.encodeURL(urlBase + language.getFlagOnSrc(req, locale)),
							out
						);
						out.print("' style='border:1px solid; vertical-align:bottom' width='");
						out.print(language.getFlagWidth(req, locale));
						out.print("' height='");
						out.print(language.getFlagHeight(req, locale));
						out.print("' alt='");
						encodeTextInXhtmlAttribute(language.getDisplay(req, locale), out);
						out.print("' /></a>");
					} else {
						out.print("&#160;<a href='");
						encodeTextInXhtmlAttribute(
							resp.encodeURL(
								UrlUtils.encodeUrlPath(
									url==null
									? (fullPath+(fullPath.indexOf('?')==-1 ? "?" : "&")+"language="+language.getCode())
									: url,
									resp.getCharacterEncoding()
								)
							),
							out
						);
						out.print("' hreflang='");
						encodeTextInXhtmlAttribute(
							language.getCode(),
							out
						);
						out.print("' onmouseover='document.images[\"flagSelector_");
						NewEncodingUtils.encodeTextInJavaScriptInXhtmlAttribute(language.getCode(), out);
						out.print("\"].src=\"");
						NewEncodingUtils.encodeTextInJavaScriptInXhtmlAttribute(resp.encodeURL(urlBase + language.getFlagOnSrc(req, locale)), out);
						out.print("\";' onmouseout='document.images[\"flagSelector_");
						out.print(language.getCode());
						out.print("\"].src=\"");
						NewEncodingUtils.encodeTextInJavaScriptInXhtmlAttribute(resp.encodeURL(urlBase + language.getFlagOffSrc(req, locale)), out);
						out.print("\";'><img src='");
						encodeTextInXhtmlAttribute(
							resp.encodeURL(urlBase + language.getFlagOffSrc(req, locale)),
							out
						);
						out.print("' id='flagSelector_");
						out.print(language.getCode());
						out.print("' style='border:1px solid; vertical-align:bottom' width='");
						out.print(language.getFlagWidth(req, locale));
						out.print("' height='");
						out.print(language.getFlagHeight(req, locale));
						out.print("' alt='");
						encodeTextInXhtmlAttribute(language.getDisplay(req, locale), out);
						out.print("' /></a>");
						ChainWriter.writeHtmlImagePreloadJavaScript(resp.encodeURL(urlBase + language.getFlagOnSrc(req, locale)), out);
					}
				}
				out.print("<br />\n");
			}
			printSearch(req, resp, out);
			out.print("          </div>\n"
					+ "          <hr />\n"
			// Display the parents
					+ "          <b>");
			out.print(applicationResources.getMessage(locale, "TextSkin.currentLocation"));
			out.print("</b><br />\n"
					+ "          <div style='white-space:nowrap'>\n");
			for(Parent parent : parents) {
				String navAlt = parent.getNavImageAlt();
				String parentPath = parent.getPath();
				if(parentPath.startsWith("/")) parentPath=parentPath.substring(1);
				out.print("            <a href='");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(urlBase + UrlUtils.encodeUrlPath(parentPath, resp.getCharacterEncoding())),
					out
				);
				out.print("'>");
				encodeTextInXhtml(navAlt, out);
				out.print("</a><br />\n");
			}
			// Always include the current page in the current location area
			out.print("            <a href='");
			encodeTextInXhtmlAttribute(
				encodedFullPath,
				out
			);
			out.print("'>");
			encodeTextInXhtml(pageAttributes.getNavImageAlt(), out);
			out.print("</a><br />\n"
					+ "          </div>\n"
					+ "          <hr />\n"
					+ "          <b>");
			out.print(applicationResources.getMessage(locale, "TextSkin.relatedPages"));
			out.print("</b><br />\n"
			// Display the siblings
					+ "          <div style='white-space:nowrap'>\n");
			List<Child> siblings = pageAttributes.getChildren();
			if(siblings.isEmpty() && !parents.isEmpty()) {
				siblings = parents.get(parents.size()-1).getChildren();
			}
			for(Child sibling : siblings) {
				String navAlt=sibling.getNavImageAlt();
				String siblingPath = sibling.getPath();
				if(siblingPath.startsWith("/")) siblingPath=siblingPath.substring(1);
				out.print("          <a href='");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(urlBase + UrlUtils.encodeUrlPath(siblingPath, resp.getCharacterEncoding())),
					out
				);
				out.print("'>");
				encodeTextInXhtml(navAlt, out);
				out.print("</a><br />\n");
			}
			out.print("          </div>\n"
					+ "          <hr />\n");
			printBelowRelatedPages(req, out);
			out.print("        </td>\n"
					+ "        <td valign='top'>\n");
			printCommonPages(req, resp, out);
		} catch(IOException err) {
			throw new JspException(err);
		} catch(SQLException err) {
			throw new JspException(err);
		}
	}

	public static void defaultPrintLinks(JspWriter out, PageAttributes pageAttributes, String encoding) throws JspException {
		try {
			for(PageAttributes.Link link : pageAttributes.getLinks()) {
				String conditionalCommentExpression = link.getConditionalCommentExpression();
				if(conditionalCommentExpression!=null) {
					out.print("    <!--[if ");
					out.print(conditionalCommentExpression);
					out.print("]>\n"
							+ "  ");
				}
				out.print("    <link rel=\"");
				encodeTextInXhtmlAttribute(link.getRel(), out);
				out.print("\" href=\"");
				encodeTextInXhtmlAttribute(UrlUtils.encodeUrlPath(link.getHref(), encoding), out);
				out.print("\" type=\"");
				encodeTextInXhtmlAttribute(link.getType(), out);
				out.print("\" />\n");
				if(conditionalCommentExpression!=null) out.print("    <![endif]-->\n");
			}
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void startContent(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes, int[] colspans, String width) throws JspException {
		try {
			out.print("          <table cellpadding='0' cellspacing='0'");
			if(width!=null && (width=width.trim()).length()>0) {
				out.print(" width='");
				out.print(width);
				out.print('\'');
			}
			out.print(">\n"
					+ "            <tr>\n");
			int totalColumns=0;
			for(int c=0;c<colspans.length;c++) {
				if(c>0) totalColumns++;
				totalColumns+=colspans[c];
			}
			out.print("              <td");
			if(totalColumns!=1) {
				out.print(" colspan='");
				out.print(totalColumns);
				out.print('\'');
			}
			out.print("><hr /></td>\n"
					+ "            </tr>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void printContentTitle(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String title, int colspan) throws JspException {
		try {
			startContentLine(req, resp, out, colspan, "center", null);
			out.print("<h1>");
			encodeTextInXhtml(title, out);
			out.print("</h1>\n");
			endContentLine(req, resp, out, 1, false);
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void startContentLine(HttpServletRequest req, HttpServletResponse resp, JspWriter out, int colspan, String align, String width) throws JspException {
		try {
			out.print("            <tr>\n"
					+ "              <td");
			if(width!=null && width.length()>0) {
				out.append(" style='width:");
				out.append(width);
				out.append('\'');
			}
			out.print(" valign='top'");
			if(colspan!=1) {
				out.print(" colspan='");
				out.print(colspan);
				out.print('\'');
			}
			if(align!=null && (align=align.trim()).length()>0) {
				out.print(" align='");
				out.print(align);
				out.print('\'');
			}
			out.print('>');
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void printContentVerticalDivider(HttpServletRequest req, HttpServletResponse resp, JspWriter out, boolean visible, int colspan, int rowspan, String align, String width) throws JspException {
		try {
			out.print("              </td>\n");
			if(visible) out.print("              <td>&#160;</td>\n");
			out.print("              <td");
			if(width!=null && width.length()>0) {
				out.append(" style='width:");
				out.append(width);
				out.append('\'');
			}
			out.print(" valign='top'");
			if(colspan!=1) {
				out.print(" colspan='");
				out.print(colspan);
				out.print('\'');
			}
			if(rowspan!=1) {
				out.print(" rowspan='");
				out.print(rowspan);
				out.print('\'');
			}
			if(align!=null && (align=align.trim()).length()>0) {
				out.print(" align='");
				out.print(align);
				out.print('\'');
			}
			out.print('>');
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void endContentLine(HttpServletRequest req, HttpServletResponse resp, JspWriter out, int rowspan, boolean endsInternal) throws JspException {
		try {
			out.print("              </td>\n"
					+ "            </tr>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void printContentHorizontalDivider(HttpServletRequest req, HttpServletResponse resp, JspWriter out, int[] colspansAndDirections, boolean endsInternal) throws JspException {
		try {
			out.print("            <tr>\n");
			for(int c=0;c<colspansAndDirections.length;c+=2) {
				if(c>0) {
					int direction=colspansAndDirections[c-1];
					switch(direction) {
						case UP:
							out.print("              <td>&#160;</td>\n");
							break;
						case DOWN:
							out.print("              <td>&#160;</td>\n");
							break;
						case UP_AND_DOWN:
							out.print("              <td>&#160;</td>\n");
							break;
						default: throw new IllegalArgumentException("Unknown direction: "+direction);
					}
				}

				int colspan=colspansAndDirections[c];
				out.print("              <td");
				if(colspan!=1) {
					out.print(" colspan='");
					out.print(colspan);
					out.print('\'');
				}
				out.print("><hr /></td>\n");
			}
			out.print("            </tr>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void endContent(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes, int[] colspans) throws JspException {
		try {
			int totalColumns=0;
			for(int c=0;c<colspans.length;c++) {
				if(c>0) totalColumns+=1;
				totalColumns+=colspans[c];
			}
			out.print("            <tr><td");
			if(totalColumns!=1) {
				out.print(" colspan='");
				out.print(totalColumns);
				out.print('\'');
			}
			out.print("><hr /></td></tr>\n");
			String copyright = pageAttributes.getCopyright();
			if(copyright!=null && copyright.length()>0) {
				out.print("            <tr><td");
				if(totalColumns!=1) {
					out.print(" colspan='");
					out.print(totalColumns);
					out.print('\'');
				}
				out.print(" align='center'><span style='font-size: x-small;'>");
				out.print(copyright);
				out.print("</span></td></tr>\n");
			}
			out.print("          </table>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void endSkin(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes) throws JspException {
		try {
			out.print("        </td>\n"
					+ "      </tr>\n"
					+ "    </table>\n");
			EditableResourceBundle.printEditableResourceBundleLookups(
				TextInJavaScriptEncoder.textInJavaScriptEncoder,
				TextInXhtmlEncoder.textInXhtmlEncoder,
				out,
				4,
				true
			);
			printGoogleAnalyticsTrackPageViewScript(req, out, SiteSettings.getInstance(req.getSession().getServletContext()).getBrand().getAowebStrutsGoogleAnalyticsNewTrackingCode());
			out.print("  </body>\n");
		} catch(IOException err) {
			throw new JspException(err);
		} catch(SQLException err) {
			throw new JspException(err);
		}
	}

	/**
	 * Reusable implemention of Google analytics pageview tracking script.
	 *
	 * @param googleAnalyticsNewTrackingCode if <code>null</code> will not print anything
	 */
	public static void printGoogleAnalyticsTrackPageViewScript(HttpServletRequest req, Appendable out, String googleAnalyticsNewTrackingCode) throws IOException {
		if(googleAnalyticsNewTrackingCode!=null) {
			Integer responseStatus = (Integer)req.getAttribute(Constants.HTTP_SERVLET_RESPONSE_STATUS);
			boolean isOk = responseStatus==null || responseStatus==HttpServletResponse.SC_OK;
			out.append("    <script type=\"text/javascript\">\n");
			if(!isOk) out.append("      // <![CDATA[\n");
			out.append("      try {\n"
					+ "        var pageTracker = _gat._getTracker(\""); out.append(googleAnalyticsNewTrackingCode); out.append("\");\n");
			if(isOk) {
				out.append("        pageTracker._trackPageview();\n");
			} else {
				assert responseStatus != null;
				out.append("        pageTracker._trackPageview(\"/");
				out.append(responseStatus.toString());
				out.append(".html?page=\"+document.location.pathname+document.location.search+\"&from=\"+document.referrer);\n");
			}
			out.append("      } catch(err) {\n"
					+ "      }\n");
			if(!isOk) out.append("      // ]]>\n");
			out.append("    </script>\n");
		}
	}

	@Override
	public void beginLightArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String width, boolean nowrap) throws JspException {
		try {
			out.print("<table style='border:5px outset #a0a0a0;");
			if(width!=null && (width=width.trim()).length()>0) {
				out.print(" width:");
				try {
					int intWidth = Integer.parseInt(width);
					out.print(intWidth);
					out.print("px");
				} catch(NumberFormatException err) {
					out.print(width);
				}
				out.print(';');
			}
			out.print("' cellpadding='0' cellspacing='0'>\n"
					+ "  <tr>\n"
					+ "    <td class='aoLightRow' style='padding:4px;");
			if(nowrap) out.print(" white-space:nowrap;");
			out.print("'>");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void endLightArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out) throws JspException {
		try {
			out.print("</td>\n"
					+ "  </tr>\n"
					+ "</table>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void beginWhiteArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out, String width, boolean nowrap) throws JspException {
		try {
			out.print("<table style='border:5px outset #a0a0a0;");
			if(width!=null && (width=width.trim()).length()>0) {
				out.print(" width:");
				try {
					int intWidth = Integer.parseInt(width);
					out.print(intWidth);
					out.print("px");
				} catch(NumberFormatException err) {
					out.print(width);
				}
				out.print(';');
			}
			out.print("' cellpadding='0' cellspacing='0'>\n"
					+ "  <tr>\n"
					+ "    <td class='aoWhiteRow' style='padding:4px;");
			if(nowrap) out.print(" white-space:nowrap;");
			out.print("'>");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void endWhiteArea(HttpServletRequest req, HttpServletResponse resp, JspWriter out) throws JspException {
		try {
			out.print("</td>\n"
					+ "  </tr>\n"
					+ "</table>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	@Override
	public void printAutoIndex(HttpServletRequest req, HttpServletResponse resp, JspWriter out, PageAttributes pageAttributes) throws JspException {
		try {
			String urlBase = getUrlBase(req);
			//Locale locale = resp.getLocale();

			out.print("<table cellpadding='0' cellspacing='10'>\n");
			List<Child> siblings = pageAttributes.getChildren();
			if(siblings.isEmpty()) {
				List<Parent> parents = pageAttributes.getParents();
				if(!parents.isEmpty()) siblings = parents.get(parents.size()-1).getChildren();
			}
			for(Child sibling : siblings) {
				String navAlt=sibling.getNavImageAlt();
				String siblingPath = sibling.getPath();
				if(siblingPath.startsWith("/")) siblingPath=siblingPath.substring(1);

				out.print("  <tr>\n"
						+ "    <td style=\"white-space:nowrap\"><a class='aoLightLink' href='");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(urlBase + UrlUtils.encodeUrlPath(siblingPath, resp.getCharacterEncoding())),
					out
				);
				out.print("'>");
				encodeTextInXhtml(navAlt, out);
				out.print("</a></td>\n"
						+ "    <td style=\"width:12px; white-space:nowrap\">&#160;</td>\n"
						+ "    <td style=\"white-space:nowrap\">");
				String description = sibling.getDescription();
				if(description!=null && (description=description.trim()).length()>0) {
					encodeTextInXhtml(description, out);
				} else {
					String title = sibling.getTitle();
					if(title!=null && (title=title.trim()).length()>0) {
						encodeTextInXhtml(title, out);
					} else {
						out.print("&#160;");
					}
				}
				out.print("</td>\n"
						+ "  </tr>\n")
				;
			}
			out.print("</table>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	/**
	 * Prints content below the related pages area on the left.
	 */
	public void printBelowRelatedPages(HttpServletRequest req, JspWriter out) throws JspException {
	}

	/**
	 * Begins a popup group.
	 *
	 * @see  #defaultBeginPopupGroup(javax.servlet.http.HttpServletRequest, javax.servlet.jsp.JspWriter, long)
	 */
	@Override
	public void beginPopupGroup(HttpServletRequest req, JspWriter out, long groupId) throws JspException {
		defaultBeginPopupGroup(req, out, groupId);
	}

	/**
	 * Default implementation of beginPopupGroup.
	 */
	public static void defaultBeginPopupGroup(HttpServletRequest req, JspWriter out, long groupId) throws JspException {
		try {
			out.print("<script type='text/javascript'>\n"
					+ "    // <![CDATA[\n"
					+ "    var popupGroupTimer"); out.print(groupId); out.print("=null;\n"
					+ "    var popupGroupAuto"); out.print(groupId); out.print("=null;\n"
					+ "    function popupGroupHideAllDetails"); out.print(groupId); out.print("() {\n"
					+ "        var spanElements = document.getElementsByTagName ? document.getElementsByTagName(\"div\") : document.all.tags(\"div\");\n"
					+ "        for (var c=0; c < spanElements.length; c++) {\n"
					+ "            if(spanElements[c].id.indexOf(\"aoPopup_"); out.print(groupId); out.print("_\")==0) {\n"
					+ "                spanElements[c].style.visibility=\"hidden\";\n"
					+ "            }\n"
					+ "        }\n"
					+ "    }\n"
					+ "    function popupGroupToggleDetails"); out.print(groupId); out.print("(popupId) {\n"
					+ "        if(popupGroupTimer"); out.print(groupId); out.print("!=null) clearTimeout(popupGroupTimer"); out.print(groupId); out.print(");\n"
					+ "        var elemStyle = document.getElementById(\"aoPopup_"); out.print(groupId); out.print("_\"+popupId).style;\n"
					+ "        if(elemStyle.visibility==\"visible\") {\n"
					+ "            elemStyle.visibility=\"hidden\";\n"
					+ "        } else {\n"
					+ "            popupGroupHideAllDetails"); out.print(groupId); out.print("();\n"
					+ "            elemStyle.visibility=\"visible\";\n"
					+ "        }\n"
					+ "    }\n"
					+ "    function popupGroupShowDetails"); out.print(groupId); out.print("(popupId) {\n"
					+ "        if(popupGroupTimer"); out.print(groupId); out.print("!=null) clearTimeout(popupGroupTimer"); out.print(groupId); out.print(");\n"
					+ "        var elemStyle = document.getElementById(\"aoPopup_"); out.print(groupId); out.print("_\"+popupId).style;\n"
					+ "        if(elemStyle.visibility!=\"visible\") {\n"
					+ "            popupGroupHideAllDetails"); out.print(groupId); out.print("();\n"
					+ "            elemStyle.visibility=\"visible\";\n"
					+ "        }\n"
					+ "    }\n"
					+ "    // ]]>\n"
					+ "</script>\n");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	/**
	 * Ends a popup group.
	 *
	 * @see  #defaultEndPopupGroup(javax.servlet.http.HttpServletRequest, javax.servlet.jsp.JspWriter, long)
	 */
	@Override
	public void endPopupGroup(HttpServletRequest req, JspWriter out, long groupId) throws JspException {
		defaultEndPopupGroup(req, out, groupId);
	}

	/**
	 * Default implementation of endPopupGroup.
	 */
	public static void defaultEndPopupGroup(HttpServletRequest req, JspWriter out, long groupId) throws JspException {
		// Nothing at the popup group end
	}

	/**
	 * Begins a popup that is in a popup group.
	 *
	 * @see  #defaultBeginPopup(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.jsp.JspWriter, long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void beginPopup(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String width) throws JspException {
		defaultBeginPopup(req, resp, out, groupId, popupId, width, getUrlBase(req));
	}

	/**
	 * Default implementation of beginPopup.
	 */
	public static void defaultBeginPopup(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String width, String urlBase) throws JspException {
		try {
			HttpSession session = req.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = getMessageResources(req);

			out.print("<div id=\"aoPopupAnchor_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("\" class=\"aoPopupAnchor\"><img class=\"aoPopupAnchorImg\" src=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + applicationResources.getMessage(locale, "TextSkin.popup.src")),
				out
			);
			out.print("\" alt=\"");
			encodeTextInXhtmlAttribute(applicationResources.getMessage(locale, "TextSkin.popup.alt"), out);
			out.print("\" width=\"");
			out.print(applicationResources.getMessage(locale, "TextSkin.popup.width"));
			out.print("\" height=\"");
			out.print(applicationResources.getMessage(locale, "TextSkin.popup.height"));
			out.print("\" onmouseover=\"popupGroupTimer");
			out.print(groupId);
			out.print("=setTimeout('popupGroupAuto");
			out.print(groupId);
			out.print("=true; popupGroupShowDetails");
			out.print(groupId);
			out.print('(');
			out.print(popupId);
			out.print(")', 1000);\" onmouseout=\"if(popupGroupAuto");
			out.print(groupId);
			out.print(") popupGroupHideAllDetails");
			out.print(groupId);
			out.print("(); if(popupGroupTimer");
			out.print(groupId);
			out.print("!=null) clearTimeout(popupGroupTimer");
			out.print(groupId);
			out.print(");\" onclick=\"popupGroupAuto");
			out.print(groupId);
			out.print("=false; popupGroupToggleDetails");
			out.print(groupId);
			out.print('(');
			out.print(popupId);
			out.print(");\" />\n"
					+ "    <div id=\"aoPopup_"); // Used to be span width=\"100%\"
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("\" class=\"aoPopupMain\" style=\"");
			if(width!=null && width.length()>0) {
				out.print("width:");
				try {
					int widthInt = Integer.parseInt(width);
					out.print(widthInt);
					out.print("px");
				} catch(NumberFormatException err) {
					out.print(width);
				}
				out.print(';');
			}
			out.print("\">\n"
					+ "        <table class=\"aoPopupTable\" cellpadding=\"0\" cellspacing=\"0\">\n"
					+ "            <tr>\n"
					+ "                <td class=\"aoPopupTL\"><img src=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_topleft.gif"),
				out
			);
			out.print("\" width=\"12\" height=\"12\" alt=\"\" /></td>\n"
					+ "                <td class=\"aoPopupTop\" style=\"background-image:url(");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_top.gif"),
				out
			);
			out.print(");\"></td>\n"
					+ "                <td class=\"aoPopupTR\"><img src=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_topright.gif"),
				out
			);
			out.print("\" width=\"12\" height=\"12\" alt=\"\" /></td>\n"
					+ "            </tr>\n"
					+ "            <tr>\n"
					+ "                <td class=\"aoPopupLeft\" style=\"background-image:url(");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_left.gif"),
				out
			);
			out.print(");\"></td>\n"
					+ "                <td class=\"aoPopupLightRow\">");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	/**
	 * Prints a popup close link/image/button for a popup that is part of a popup group.
	 *
	 * @see  #defaultPrintPopupClose(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.jsp.JspWriter, long, long, java.lang.String)
	 */
	@Override
	public void printPopupClose(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId) throws JspException {
		defaultPrintPopupClose(req, resp, out, groupId, popupId, getUrlBase(req));
	}

	/**
	 * Default implementation of printPopupClose.
	 */
	public static void defaultPrintPopupClose(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String urlBase) throws JspException {
		try {
			HttpSession session = req.getSession();
			Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
			MessageResources applicationResources = getMessageResources(req);

			out.print("<img class=\"aoPopupClose\" src=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + applicationResources.getMessage(locale, "TextSkin.popupClose.src")),
				out
			);
			out.print("\" alt=\"");
			encodeTextInXhtmlAttribute(applicationResources.getMessage(locale, "TextSkin.popupClose.alt"), out);
			out.print("\" width=\"");
			out.print(applicationResources.getMessage(locale, "TextSkin.popupClose.width"));
			out.print("\" height=\"");
			out.print(applicationResources.getMessage(locale, "TextSkin.popupClose.height"));
			out.print("\" onclick=\"popupGroupHideAllDetails");
			out.print(groupId);
			out.print("();\" />");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}

	/**
	 * Ends a popup that is in a popup group.
	 *
	 * @see  #defaultEndPopup(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.jsp.JspWriter, long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void endPopup(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String width) throws JspException {
		TextSkin.defaultEndPopup(req, resp, out, groupId, popupId, width, getUrlBase(req));
	}

	/**
	 * Default implementation of endPopup.
	 */
	public static void defaultEndPopup(HttpServletRequest req, HttpServletResponse resp, JspWriter out, long groupId, long popupId, String width, String urlBase) throws JspException {
		try {
			out.print("</td>\n"
					+ "                <td class=\"aoPopupRight\" style=\"background-image:url(");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_right.gif"),
				out
			);
			out.print(");\"></td>\n"
					+ "            </tr>\n"
					+ "            <tr>\n" 
					+ "                <td class=\"aoPopupBL\"><img src=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_bottomleft.gif"),
				out
			);
			out.print("\" width=\"12\" height=\"12\" alt=\"\" /></td>\n"
					+ "                <td class=\"aoPopupBottom\" style=\"background-image:url(");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_bottom.gif"),
				out
			);
			out.print(");\"></td>\n"
					+ "                <td class=\"aoPopupBR\"><img src=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(urlBase + "textskin/popup_bottomright.gif"),
				out
			);
			out.print("\" width=\"12\" height=\"12\" alt=\"\" /></td>\n"
					+ "            </tr>\n"
					+ "        </table>\n"
					+ "    </div>\n"
					+ "</div>\n"
					+ "<script type='text/javascript'>\n"
					+ "    // <![CDATA[\n"
					+ "    // Override onload\n"
					+ "    var aoPopupOldOnload_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(" = window.onload;\n"
					+ "    function adjustPositionOnload_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("() {\n"
					+ "        adjustPosition_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("();\n"
					+ "        if(aoPopupOldOnload_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(") {\n"
					+ "            aoPopupOldOnload_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("();\n"
					+ "            aoPopupOldOnload_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(" = null;\n"
					+ "        }\n"
					+ "    }\n"
					+ "    window.onload = adjustPositionOnload_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(";\n"
					+ "    // Override onresize\n"
					+ "    var aoPopupOldOnresize_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(" = window.onresize;\n"
					+ "    function adjustPositionOnresize_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("() {\n"
					+ "        adjustPosition_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("();\n"
					+ "        if(aoPopupOldOnresize_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(") {\n"
					+ "            aoPopupOldOnresize_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("();\n"
					+ "        }\n"
					+ "    }\n"
					+ "    window.onresize = adjustPositionOnresize_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print(";\n"
					+ "    function adjustPosition_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("() {\n"
					+ "        var popupAnchor = document.getElementById(\"aoPopupAnchor_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("\");\n"
					+ "        var popup = document.getElementById(\"aoPopup_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("\");\n"
					+ "        // Find the screen position of the anchor\n"
					+ "        var popupAnchorLeft = 0;\n"
					+ "        var obj = popupAnchor;\n"
					+ "        if(obj.offsetParent) {\n"
					+ "            popupAnchorLeft = obj.offsetLeft\n"
					+ "            while (obj = obj.offsetParent) {\n"
					+ "                popupAnchorLeft += obj.offsetLeft\n"
					+ "            }\n"
					+ "        }\n"
					+ "        var popupAnchorRight = popupAnchorLeft + popupAnchor.offsetWidth;\n"
					+ "        // Find the width of the popup\n"
					+ "        var popupWidth = popup.offsetWidth;\n"
					+ "        // Find the width of the screen\n"
					+ "        var screenWidth = (document.compatMode && document.compatMode == \"CSS1Compat\") ? document.documentElement.clientWidth : document.body.clientWidth;\n"
					+ "        // Find the desired screen position of the popup\n"
					+ "        var popupScreenPosition = 0;\n"
					+ "        if(screenWidth<=(popupWidth+12)) {\n"
					+ "            popupScreenPosition = 0;\n"
					+ "        } else {\n"
					+ "            popupScreenPosition = screenWidth - popupWidth - 12;\n"
					+ "            if(popupAnchorRight < popupScreenPosition) popupScreenPosition = popupAnchorRight;\n"
					+ "        }\n"
					+ "        popup.style.left=(popupScreenPosition-popupAnchorLeft)+\"px\";\n"
					+ "    }\n"
					+ "    // Call once at parse time for when the popup is activated while page loading (before onload called)\n"
					+ "    adjustPosition_");
			out.print(groupId);
			out.print('_');
			out.print(popupId);
			out.print("();\n"
					+ "  // ]]>\n"
					+ "</script>");
		} catch(IOException err) {
			throw new JspException(err);
		}
	}
}
