/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2013, 2015, 2016, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.website;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.encoding.Doctype;
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute;
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.encoding.Serialization;
import static com.aoindustries.encoding.TextInJavaScriptEncoder.textInJavaScriptEncoder;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.encoding.servlet.SerializationEE;
import com.aoindustries.html.Document;
import com.aoindustries.html.LINK;
import com.aoindustries.html.META;
import com.aoindustries.html.SCRIPT;
import com.aoindustries.html.STYLE;
import com.aoindustries.html.util.GoogleAnalytics;
import com.aoindustries.html.util.ImagePreload;
import com.aoindustries.io.NoCloseWriter;
import static com.aoindustries.lang.Strings.trimNullIfEmpty;
import com.aoindustries.net.AnyURI;
import com.aoindustries.net.EmptyURIParameters;
import com.aoindustries.net.URIEncoder;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import com.aoindustries.servlet.lastmodified.LastModifiedUtil;
import com.aoindustries.style.AoStyle;
import static com.aoindustries.taglib.AttributeUtils.appendWidthStyle;
import com.aoindustries.taglib.GlobalAttributes;
import com.aoindustries.taglib.HtmlTag;
import com.aoindustries.util.i18n.EditableResourceBundle;
import com.aoindustries.web.resources.registry.Group;
import com.aoindustries.web.resources.registry.Registry;
import com.aoindustries.web.resources.registry.Style;
import com.aoindustries.web.resources.renderer.Renderer;
import com.aoindustries.web.resources.servlet.RegistryEE;
import com.aoindustries.website.skintags.Child;
import com.aoindustries.website.skintags.PageAttributes;
import com.aoindustries.website.skintags.Parent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import org.apache.struts.util.MessageResources;

/**
 * The skin for the home page of the site.
 *
 * @author  AO Industries, Inc.
 */
public class TextSkin extends Skin {

	// Matches TextOnlyLayout.NAME
	public static final String NAME = "Text";

	/**
	 * The name of the {@linkplain com.aoindustries.web.resources.servlet.RegistryEE.Application application-scope}
	 * group that will be used for text skin web resources.
	 */
	public static final Group.Name RESOURCE_GROUP = new Group.Name(TextSkin.class.getName());

	public static final Style TEXTSKIN_CSS = new Style("/textskin/textskin.css");

	@WebListener
	public static class Initializer implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent event) {
			RegistryEE.Application.get(event.getServletContext())
				.getGroup(RESOURCE_GROUP)
				.styles
				.add(
					AoStyle.AO_STYLE,
					TEXTSKIN_CSS
				);
		}
		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Do nothing
		}
	}

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
		return NAME;
	}

	@Override
	public String getDisplay(HttpServletRequest req) throws JspException {
		Locale locale = LocaleAction.getLocale(req.getServletContext(), req);
		MessageResources applicationResources = (MessageResources)req.getAttribute("/ApplicationResources");
		if(applicationResources==null) throw new JspException("Unable to load resources: /ApplicationResources");
		return applicationResources.getMessage(locale, "TextSkin.name");
	}

	/**
	 * Print the logo for the top left part of the page.
	 */
	public void printLogo(HttpServletRequest req, HttpServletResponse resp, Document document, String urlBase) throws JspException, IOException {
		// Print no logo by default
	}

	/**
	 * Prints the search form, if any exists.
	 */
	public void printSearch(HttpServletRequest req, HttpServletResponse resp, Document document) throws JspException, IOException {
	}

	/**
	 * Prints the common pages area, which is at the top of the site.
	 */
	public void printCommonPages(HttpServletRequest req, HttpServletResponse resp, Document document) throws JspException, IOException {
	}

	/**
	 * Prints the lines for any JavaScript sources.
	 */
	public void printJavaScriptSources(HttpServletRequest req, HttpServletResponse resp, Document document, String urlBase) throws JspException, IOException {
	}

	/**
	 * Prints the line for the favicon.
	 */
	public void printFavIcon(HttpServletRequest req, HttpServletResponse resp, Document document, String urlBase) throws JspException, IOException {
	}

	public static MessageResources getMessageResources(HttpServletRequest req) throws JspException {
		MessageResources resources = (MessageResources)req.getAttribute("/ApplicationResources");
		if(resources==null) throw new JspException("Unable to load resources: /ApplicationResources");
		return resources;
	}

	@Override
	public void configureResources(ServletContext servletContext, HttpServletRequest req, HttpServletResponse resp, Registry requestRegistry, PageAttributes page) {
		super.configureResources(servletContext, req, resp, requestRegistry, page);
		requestRegistry.activate(RESOURCE_GROUP);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void startSkin(HttpServletRequest req, HttpServletResponse resp, Document document, PageAttributes pageAttributes) throws JspException, IOException {
		try {
			ServletContext servletContext = req.getServletContext();
			SiteSettings settings = SiteSettings.getInstance(servletContext);
			Brand brand = settings.getBrand();
			String trackingId = brand.getAowebStrutsGoogleAnalyticsNewTrackingCode();
			// Write doctype
			document.xmlDeclaration(resp.getCharacterEncoding());
			document.doctype();
			// Write <html>
			HtmlTag.beginHtmlTag(resp, document.out, document.serialization, (GlobalAttributes)null); document.out.write("\n"
			+ "  <head>\n");

			String layout = pageAttributes.getLayout();
			if(!layout.equals(PageAttributes.LAYOUT_NORMAL)) throw new JspException("TODO: Implement layout: "+layout);
			Locale locale = LocaleAction.getLocale(servletContext, req);
			MessageResources applicationResources = getMessageResources(req);
			String urlBase = getUrlBase(req);
			String path = pageAttributes.getPath();
			if(path.startsWith("/")) path=path.substring(1);
			final String fullPath = urlBase + path;
			final String encodedFullPath = resp.encodeURL(URIEncoder.encodeURI(fullPath));
			List<Skin> skins = settings.getSkins();
			boolean isOkResponseStatus = (resp.getStatus() == HttpServletResponse.SC_OK);

			// If this is not the default skin, then robots noindex
			boolean robotsMetaUsed = false;
			if(!isOkResponseStatus || !getName().equals(skins.get(0).getName())) {
				document.out.write("    "); document.meta(META.Name.ROBOTS).content("noindex, nofollow").__().nl();
			}
			if(document.doctype == Doctype.HTML5) {
				document.out.write("    "); document.meta().charset(resp.getCharacterEncoding()).__().nl();
			} else {
				document.out.write("    "); document.meta(META.HttpEquiv.CONTENT_TYPE).content(resp.getContentType()).__().out.write("\n"
				// Default style language
				+ "    "); document.meta(META.HttpEquiv.CONTENT_STYLE_TYPE).content(STYLE.Type.TEXT_CSS).__().out.write("\n"
				+ "    "); document.meta(META.HttpEquiv.CONTENT_SCRIPT_TYPE).content(SCRIPT.Type.TEXT_JAVASCRIPT).__().nl();
			}
			if(document.doctype == Doctype.HTML5) {
				GoogleAnalytics.writeGlobalSiteTag(document, trackingId);
			} else {
				GoogleAnalytics.writeAnalyticsJs(document, trackingId);
			}
			// Mobile support
			document.out.write("    "); document.meta(META.Name.VIEWPORT).content("width=device-width, initial-scale=1.0").__().out.write("\n"
			+ "    "); document.meta(META.Name.APPLE_MOBILE_WEB_APP_CAPABLE).content("yes").__().out.write("\n"
			+ "    "); document.meta(META.Name.APPLE_MOBILE_WEB_APP_STATUS_BAR_STYLE).content("black").__().nl();
			// Authors
			// TODO: dcterms copyright
			String author = pageAttributes.getAuthor();
			if(author != null && !(author = author.trim()).isEmpty()) {
				document.out.write("    "); document.meta(META.Name.AUTHOR).content(author).__().nl();
			}
			String authorHref = pageAttributes.getAuthorHref();
			if(authorHref != null && !(authorHref = authorHref.trim()).isEmpty()) {
				document.out.write("    "); document.link(LINK.Rel.AUTHOR).href(
					// TODO: RFC 3986-only always?
					resp.encodeURL(
						URIEncoder.encodeURI(authorHref)
					)
				).__().nl();
			}
			document.out.write("    <title>");
			// No more page stack, just show current page only
			/*
			List<Parent> parents = pageAttributes.getParents();
			for(Parent parent : parents) {
				html.text(parent.getTitle());
				out.print(" - ");
			}
			 */
			document.text(pageAttributes.getTitle()); document.out.write("</title>\n");
			String description = pageAttributes.getDescription();
			if(description != null && !(description = description.trim()).isEmpty()) {
				document.out.write("    "); document.meta(META.Name.DESCRIPTION).content(description).__().nl();
			}
			String keywords = pageAttributes.getKeywords();
			if(keywords != null && !(keywords = keywords.trim()).isEmpty()) {
				document.out.write("    "); document.meta(META.Name.KEYWORDS).content(keywords).__().nl();
			}
			// TODO: Review HTML 4/HTML 5 differences from here
			// If this is an authenticated page, redirect to session timeout after one hour
			AOServConnector aoConn = AuthenticatedAction.getAoConn(req, resp);
			HttpSession session = req.getSession(false);
			//if(session == null) session = req.getSession(false); // Get again, just in case of authentication
			if(isOkResponseStatus && aoConn != null && session != null) {
				document.out.write("    "); document.meta(META.HttpEquiv.REFRESH).content(content -> {
					content.write(Integer.toString(Math.max(60, session.getMaxInactiveInterval() - 60)));
					content.write(";URL=");
					content.write(
						resp.encodeRedirectURL(
							URIEncoder.encodeURI(
								urlBase
								+ "session-timeout.do?target="
								+ URIEncoder.encodeURIComponent(fullPath)
							)
						)
					);
				}).__().nl();
			}
			for(com.aoindustries.website.skintags.Meta meta : pageAttributes.getMetas()) {
				// Skip robots if not on default skin
				boolean isRobots = meta.getName().equalsIgnoreCase("robots");
				if(!robotsMetaUsed || !isRobots) {
					document.out.write("    "); document.meta().name(meta.getName()).content(meta.getContent()).__().nl();
					if(isRobots) robotsMetaUsed = true;
				}
			}
			if(isOkResponseStatus) {
				String googleVerify = brand.getAowebStrutsGoogleVerifyContent();
				if(googleVerify != null) {
					document.out.write("    "); document.meta().name("verify-v1").content(googleVerify).__().nl();
				}
			}
			String copyright = pageAttributes.getCopyright();
			if(copyright != null && !(copyright = copyright.trim()).isEmpty()) {
				// TODO: Dublin Core: https://stackoverflow.com/questions/6665312/is-the-copyright-meta-tag-valid-in-html5
				document.out.write("    "); document.meta().name("copyright").content(copyright).__().nl();
			}
			List<Language> languages = settings.getLanguages(req);
			printAlternativeLinks(req, resp, document, fullPath, languages);

			// Configure skin resources
			Registry requestRegistry = RegistryEE.Request.get(servletContext, req);
			configureResources(servletContext, req, resp, requestRegistry, pageAttributes);
			// Configure page resources
			Registry pageRegistry = RegistryEE.Page.get(req);
			if(pageRegistry == null) throw new JspException("page-scope registry not found.  PageAction.execute(…) invoked?");
			// Render links
			document.out.write("    "); Renderer.get(servletContext).renderStyles(
				req,
				resp,
				document,
				"    ",
				true, // registeredActivations
				null, // No additional activations
				requestRegistry, // request-scope
				RegistryEE.Session.get(req.getSession(false)), // session-scope
				pageRegistry
			); document.nl();

			defaultPrintLinks(servletContext, req, resp, document, pageAttributes);
			printJavaScriptSources(req, resp, document, urlBase);
			document.out.write("    "); document.script().src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase + "commons-validator-1.3.1-compress.js"
					)
				)
			).__().nl();
			printFavIcon(req, resp, document, urlBase);
			// TODO: Canonical?
			document.out.write("  </head>\n"
			+ "  <body");
			String onload = pageAttributes.getOnload();
			if(onload != null && !(onload = onload.trim()).isEmpty()) {
				document.out.write(" onload=\""); encodeJavaScriptInXhtmlAttribute(onload, document.out); document.out.write('"');
			}
			document.out.write(">\n"
			+ "    <table cellspacing=\"10\" cellpadding=\"0\">\n"
			+ "      <tr>\n"
			+ "        <td valign=\"top\">\n");
			printLogo(req, resp, document, urlBase);
			if(aoConn != null) {
				document.out.write("          "); document.hr__().out.write("\n"
				+ "          "); document.text(applicationResources.getMessage(locale, "TextSkin.logoutPrompt"))
				.out.write("<form style=\"display:inline\" id=\"logout_form\" method=\"post\" action=\"");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(
						URIEncoder.encodeURI(
							urlBase + "logout.do"
						)
					),
					document.out
				);
				document.out.write("\"><div style=\"display:inline;\">");
				document.input().hidden().name("target").value(fullPath).__()
				// Variant that takes ResourceBundle?
				.input().submit__(applicationResources.getMessage(locale, "TextSkin.logoutButtonLabel"))
				.out.write("</div></form>\n");
			} else {
				document.out.write("          "); document.hr__().out.write("\n"
				+ "          "); document.text(applicationResources.getMessage(locale, "TextSkin.loginPrompt"))
				.out.write("<form style=\"display:inline\" id=\"login_form\" method=\"post\" action=\"");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(
						URIEncoder.encodeURI(
							urlBase + "login.do"
						)
					),
					document.out
				);
				document.out.write("\"><div style=\"display:inline\">");
				// Only include the target when they are not in the /clientarea/ part of the site
				if(path.startsWith("clientarea/")) {
					document.input().hidden().name("target").value(fullPath).__();
				}
				document.input().submit__(applicationResources.getMessage(locale, "TextSkin.loginButtonLabel"))
				.out.write("</div></form>\n");
			}
			document.out.write("          "); document.hr__().nl()
			.out.write("          <div style=\"white-space: nowrap\">\n");
			if(skins.size() > 1) {
				document.script().out(script -> {
					script.append("function selectLayout(layout) {\n");
					for(Skin skin : skins) {
						script.append("  if(layout==").text(skin.getName()).append(") window.top.location.href=").text(
							resp.encodeURL(
								new AnyURI(fullPath)
									.addEncodedParameter(Constants.LAYOUT, URIEncoder.encodeURIComponent(skin.getName()))
									.toASCIIString()
							)
						).append(";\n");
					}
					script.append('}');
				}).__().nl()
				.out.write("            <form action=\"\" style=\"display:inline\"><div style=\"display:inline\">\n"
				+ "              "); document.text(applicationResources.getMessage(locale, "TextSkin.layoutPrompt"))
				.out.write("<select name=\"layout_selector\" onchange=\"selectLayout(this.form.layout_selector.options[this.form.layout_selector.selectedIndex].value);\">\n");
				for(Skin skin : skins) {
					document.out.write("                "); document.option().value(skin.getName()).selected(getName().equals(skin.getName())).text__(skin.getDisplay(req)).nl();
				}
				document.out.write("              </select>\n"
				+ "            </div></form>"); document.br__().nl();
			}
			if(languages.size()>1) {
				document.out.write("            ");
				for(Language language : languages) {
					AnyURI uri = language.getUri();
					if(language.getCode().equalsIgnoreCase(locale.getLanguage())) {
						document.out.write("&#160;<a href=\"");
						encodeTextInXhtmlAttribute(
							resp.encodeURL(
								URIEncoder.encodeURI(
									(
										uri == null
										? new AnyURI(fullPath).addEncodedParameter(Constants.LANGUAGE, URIEncoder.encodeURIComponent(language.getCode()))
										: uri
									).toASCIIString()
								)
							),
							document.out
						);
						document.out.write("\" hreflang=\""); encodeTextInXhtmlAttribute(language.getCode(), document.out); document.out.write("\">");
						document.img()
							.src(
								resp.encodeURL(
									URIEncoder.encodeURI(
										urlBase + language.getFlagOnSrc(req, locale)
									)
								)
							).style("border:1px solid; vertical-align:bottom")
							.width(language.getFlagWidth(req, locale))
							.height(language.getFlagHeight(req, locale))
							.alt(language.getDisplay(req, locale))
							.__()
						.out.write("</a>");
					} else {
						document.out.write("&#160;<a href=\"");
						encodeTextInXhtmlAttribute(
							resp.encodeURL(
								URIEncoder.encodeURI(
									(
										uri == null
										? new AnyURI(fullPath).addEncodedParameter(Constants.LANGUAGE, URIEncoder.encodeURIComponent(language.getCode()))
										: uri
									).toASCIIString()
								)
							),
							document.out
						);
						document.out.write("\" hreflang=\""); encodeTextInXhtmlAttribute(language.getCode(), document.out); document.out.write("\" onmouseover=\"");
						try (MediaWriter onmouseover = new MediaWriter(document.encodingContext, javaScriptInXhtmlAttributeEncoder, new NoCloseWriter(document.out))) {
							onmouseover.append("document.images[").text("flagSelector_" + language.getCode()).append("].src=").text(
								resp.encodeURL(
									URIEncoder.encodeURI(
										urlBase
										+ language.getFlagOnSrc(req, locale)
									)
								)
							).append(';');
						} document.out.write("\" onmouseout=\""); try (MediaWriter onmouseout = new MediaWriter(document.encodingContext, javaScriptInXhtmlAttributeEncoder, new NoCloseWriter(document.out))) {
							onmouseout.append("document.images[").text("flagSelector_" + language.getCode()).append("].src=").text(
								resp.encodeURL(
									URIEncoder.encodeURI(
										urlBase
										+ language.getFlagOffSrc(req, locale)
									)
								)
							).append(';');
						} document.out.write("\">"); document.img()
							.src(
								resp.encodeURL(
									URIEncoder.encodeURI(
										urlBase
										+ language.getFlagOffSrc(req, locale)
									)
								)
							).id("flagSelector_" + language.getCode())
							.style("border:1px solid; vertical-align:bottom")
							.width(language.getFlagWidth(req, locale))
							.height(language.getFlagHeight(req, locale))
							.alt(language.getDisplay(req, locale))
							.__()
						.out.write("</a>");
						ImagePreload.writeImagePreloadScript(
							resp.encodeURL(
								URIEncoder.encodeURI(
									urlBase + language.getFlagOnSrc(req, locale)
								)
							),
							document
						);
					}
				}
				document.br__().nl();
			}
			printSearch(req, resp, document);
			document.out.write("          </div>\n"
			+ "          "); document.hr__().out.write("\n"
			// Display the parents
			+ "          <b>"); document.text(applicationResources.getMessage(locale, "TextSkin.currentLocation")).out.write("</b>"); document.br__().out.write("\n"
			+ "          <div style=\"white-space:nowrap\">\n");
			List<Parent> parents = pageAttributes.getParents();
			for(Parent parent : parents) {
				String navAlt = parent.getNavImageAlt();
				String parentPath = parent.getPath();
				if(parentPath.startsWith("/")) parentPath=parentPath.substring(1);
				document.out.write("            <a href=\"");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(
						URIEncoder.encodeURI(
							urlBase
							+ URIEncoder.encodeURI(parentPath)
						)
					),
					document.out
				);
				document.out.write("\">"); document.text(navAlt).out.write("</a>"); document.br__().nl();
			}
			// Always include the current page in the current location area
			document.out.write("            <a href=\""); encodeTextInXhtmlAttribute(encodedFullPath, document.out); document.out.write("\">");
			document.text(pageAttributes.getNavImageAlt())
			.out.write("</a>"); document.br__().out.write("\n"
			+ "          </div>\n"
			+ "          "); document.hr__().out.write("\n"
			+ "          <b>"); document.text(applicationResources.getMessage(locale, "TextSkin.relatedPages")).out.write("</b>"); document.br__().out.write("\n"
			// Display the siblings
			+ "          <div style=\"white-space:nowrap\">\n");
			List<Child> siblings = pageAttributes.getChildren();
			if(siblings.isEmpty() && !parents.isEmpty()) {
				siblings = parents.get(parents.size()-1).getChildren();
			}
			for(Child sibling : siblings) {
				String navAlt = sibling.getNavImageAlt();
				String siblingPath = sibling.getPath();
				if(siblingPath.startsWith("/")) siblingPath = siblingPath.substring(1);
				document.out.write("          <a href=\"");
				encodeTextInXhtmlAttribute(
					resp.encodeURL(
						URIEncoder.encodeURI(
							urlBase
							+ URIEncoder.encodeURI(siblingPath)
						)
					),
					document.out
				);
				document.out.write("\">"); document.text(navAlt).out.write("</a>"); document.br__().nl();
			}
			document.out.write("          </div>\n");
			document.hr__().nl();
			printBelowRelatedPages(req, document);
			document.out.write("        </td>\n"
			+ "        <td valign=\"top\">\n");
			printCommonPages(req, resp, document);
		} catch(SQLException err) {
			throw new JspException(err);
		}
	}

	public static void defaultPrintLinks(ServletContext servletContext, HttpServletRequest req, HttpServletResponse resp, Document document, PageAttributes pageAttributes) throws JspException, IOException {
		for(PageAttributes.Link link : pageAttributes.getLinks()) {
			String href = link.getHref();
			String rel = link.getRel();
			document.out.write("    "); document.link()
				.rel(rel)
				.href(href == null ? null :
					LastModifiedUtil.buildURL(
						servletContext,
						req,
						resp,
						href,
						EmptyURIParameters.getInstance(),
						AddLastModified.AUTO,
						false,
						// TODO: Support canonical flag on link
						LINK.Rel.CANONICAL.toString().equalsIgnoreCase(rel)
					)
				)
				.type(link.getType())
				.__().nl();
		}
	}

	@Override
	public void startContent(HttpServletRequest req, HttpServletResponse resp, Document document, PageAttributes pageAttributes, int[] colspans, String width) throws JspException, IOException {
		width = trimNullIfEmpty(width);
		document.out.write("          <table class=\"ao-packed\"");
		if(width != null) {
			document.out.write(" style=\""); appendWidthStyle(width, document.out); document.out.write('"');
		}
		document.out.write(">\n"
		+ "            <tr>\n");
		int totalColumns = 0;
		for(int c = 0; c < colspans.length; c++) {
			if(c > 0) totalColumns++;
			totalColumns += colspans[c];
		}
		document.out.write("              <td");
		if(totalColumns != 1) document.out.append(" colspan=\"").append(Integer.toString(totalColumns)).append('"');
		document.out.write('>'); document.hr__().out.write("</td>\n"
		+ "            </tr>\n");
	}

	@Override
	public void printContentTitle(HttpServletRequest req, HttpServletResponse resp, Document document, String title, int colspan) throws JspException, IOException {
		startContentLine(req, resp, document, colspan, "center", null);
		document.h1__(title).nl();
		endContentLine(req, resp, document, 1, false);
	}

	@Override
	public void startContentLine(HttpServletRequest req, HttpServletResponse resp, Document document, int colspan, String align, String width) throws JspException, IOException {
		align = trimNullIfEmpty(align);
		width = trimNullIfEmpty(width);
		document.out.write("            <tr>\n"
		+ "              <td");
		if(align != null || width != null) {
			document.out.write(" style=\"");
			if(align != null) {
				document.out.write("text-align:");
				encodeTextInXhtmlAttribute(align, document.out);
			}
			if(width != null) {
				if(align != null) document.out.write(';');
				appendWidthStyle(width, document.out);
			}
			document.out.write('"');
		}
		document.out.write(" valign=\"top\"");
		if(colspan != 1) document.out.append(" colspan=\"").append(Integer.toString(colspan)).append('"');
		document.out.write('>');
	}

	@Override
	public void printContentVerticalDivider(HttpServletRequest req, HttpServletResponse resp, Document document, boolean visible, int colspan, int rowspan, String align, String width) throws JspException, IOException {
		align = trimNullIfEmpty(align);
		width = trimNullIfEmpty(width);
		document.out.write("              </td>\n");
		if(visible) document.out.write("              <td>&#160;</td>\n");
		document.out.write("              <td");
		if(align != null || width != null) {
			document.out.write(" style=\"");
			if(align != null) {
				document.out.write("text-align:");
				encodeTextInXhtmlAttribute(align, document.out);
			}
			if(width != null) {
				if(align != null) document.out.write(';');
				appendWidthStyle(width, document.out);
			}
			document.out.write('"');
		}
		document.out.write(" valign=\"top\"");
		if(colspan != 1) document.out.append(" colspan=\"").append(Integer.toString(colspan)).append('"');
		if(rowspan != 1) document.out.append(" rowspan=\"").append(Integer.toString(rowspan)).append('"');
		document.out.write('>');
	}

	@Override
	public void endContentLine(HttpServletRequest req, HttpServletResponse resp, Document document, int rowspan, boolean endsInternal) throws JspException, IOException {
		document.out.write("              </td>\n"
		+ "            </tr>\n");
	}

	@Override
	public void printContentHorizontalDivider(HttpServletRequest req, HttpServletResponse resp, Document document, int[] colspansAndDirections, boolean endsInternal) throws JspException, IOException {
		document.out.write("            <tr>\n");
		for(int c = 0; c < colspansAndDirections.length; c += 2) {
			if(c > 0) {
				int direction = colspansAndDirections[c - 1];
				switch(direction) {
					case UP:
						document.out.write("              <td>&#160;</td>\n");
						break;
					case DOWN:
						document.out.write("              <td>&#160;</td>\n");
						break;
					case UP_AND_DOWN:
						document.out.write("              <td>&#160;</td>\n");
						break;
					default: throw new IllegalArgumentException("Unknown direction: " + direction);
				}
			}

			int colspan = colspansAndDirections[c];
			document.out.write("              <td");
			if(colspan != 1) document.out.append(" colspan=\"").append(Integer.toString(colspan)).append('"');
			document.out.write('>'); document.hr__().out.write("</td>\n");
		}
		document.out.write("            </tr>\n");
	}

	@Override
	public void endContent(HttpServletRequest req, HttpServletResponse resp, Document document, PageAttributes pageAttributes, int[] colspans) throws JspException, IOException {
		int totalColumns = 0;
		for(int c = 0; c < colspans.length; c++) {
			if(c > 0) totalColumns += 1;
			totalColumns += colspans[c];
		}
		document.out.write("            <tr><td");
		if(totalColumns != 1) document.out.append(" colspan=\"").append(Integer.toString(totalColumns)).append('"');
		document.out.write('>'); document.hr__().out.write("</td></tr>\n");
		String copyright = pageAttributes.getCopyright();
		if(copyright != null && !(copyright = copyright.trim()).isEmpty()) {
			document.out.write("            <tr><td");
			if(totalColumns != 1) document.out.append(" colspan=\"").append(Integer.toString(totalColumns)).append('"');
			document.out.write(" style=\"text-align:center\"><span style=\"font-size: x-small\">");
			document.text(copyright).out.write("</span></td></tr>\n");
		}
		document.out.write("          </table>\n");
	}

	@Override
	public void endSkin(HttpServletRequest req, HttpServletResponse resp, Document document, PageAttributes pageAttributes) throws JspException, IOException {
		document.out.write("        </td>\n"
		+ "      </tr>\n"
		+ "    </table>\n");
		// TODO: SemanticCMS component for this, also make sure in other layouts and skins
		EditableResourceBundle.printEditableResourceBundleLookups(
			textInJavaScriptEncoder,
			textInXhtmlEncoder,
			document.out,
			SerializationEE.get(req.getServletContext(), req) == Serialization.XML,
			4,
			true
		);
		document.out.write("  </body>\n");
		HtmlTag.endHtmlTag(document.out); document.nl();
	}

	@Override
	public void beginLightArea(HttpServletRequest req, HttpServletResponse resp, Document document, String align, String width, boolean nowrap) throws JspException, IOException {
		align = trimNullIfEmpty(align);
		width = trimNullIfEmpty(width);
		document.out.write("<table class=\"ao-packed\" style=\"border:5px outset #a0a0a0");
		if(width != null) {
			document.out.write(';');
			appendWidthStyle(width, document.out);
		}
		document.out.write("\">\n"
		+ "  <tr>\n"
		+ "    <td class=\"aoLightRow\" style=\"padding:4px");
		if(align != null) {
			document.out.write(";text-align:");
			encodeTextInXhtmlAttribute(align, document.out);
		}
		if(nowrap) document.out.write(";white-space:nowrap;");
		document.out.write("\">");
	}

	@Override
	public void endLightArea(HttpServletRequest req, HttpServletResponse resp, Document document) throws JspException, IOException {
		document.out.write("</td>\n"
		+ "  </tr>\n"
		+ "</table>\n");
	}

	@Override
	public void beginWhiteArea(HttpServletRequest req, HttpServletResponse resp, Document document, String align, String width, boolean nowrap) throws JspException, IOException {
		align = trimNullIfEmpty(align);
		width = trimNullIfEmpty(width);
		document.out.write("<table class=\"ao-packed\" style=\"border:5px outset #a0a0a0");
		if(width != null) {
			document.out.write(';');
			appendWidthStyle(width, document.out);
		}
		document.out.write("\">\n"
		+ "  <tr>\n"
		+ "    <td class=\"aoWhiteRow\" style=\"padding:4px;");
		if(align != null) {
			document.out.write(";text-align:");
			encodeTextInXhtmlAttribute(align, document.out);
		}
		if(nowrap) document.out.write(" white-space:nowrap;");
		document.out.write("\">");
	}

	@Override
	public void endWhiteArea(HttpServletRequest req, HttpServletResponse resp, Document document) throws JspException, IOException {
		document.out.write("</td>\n"
		+ "  </tr>\n"
		+ "</table>\n");
	}

	@Override
	public void printAutoIndex(HttpServletRequest req, HttpServletResponse resp, Document document, PageAttributes pageAttributes) throws JspException, IOException {
		String urlBase = getUrlBase(req);
		//Locale locale = resp.getLocale();

		document.out.write("<table cellpadding=\"0\" cellspacing=\"10\">\n");
		List<Child> siblings = pageAttributes.getChildren();
		if(siblings.isEmpty()) {
			List<Parent> parents = pageAttributes.getParents();
			if(!parents.isEmpty()) siblings = parents.get(parents.size()-1).getChildren();
		}
		for(Child sibling : siblings) {
			String navAlt=sibling.getNavImageAlt();
			String siblingPath = sibling.getPath();
			if(siblingPath.startsWith("/")) siblingPath=siblingPath.substring(1);

			document.out.write("  <tr>\n"
			+ "    <td style=\"white-space:nowrap\"><a class=\"aoLightLink\" href=\"");
			encodeTextInXhtmlAttribute(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase
						+ URIEncoder.encodeURI(siblingPath)
					)
				),
				document.out
			);
			document.out.write("\">"); document.text(navAlt).out.write("</a></td>\n"
			+ "    <td style=\"width:12px; white-space:nowrap\">&#160;</td>\n"
			+ "    <td style=\"white-space:nowrap\">");
			String description = sibling.getDescription();
			if(description != null && !(description = description.trim()).isEmpty()) {
				document.text(description);
			} else {
				String title = sibling.getTitle();
				if(title != null && !(title = title.trim()).isEmpty()) {
					document.text(title);
				} else {
					document.out.write("&#160;");
				}
			}
			document.out.write("</td>\n"
			+ "  </tr>\n");
		}
		document.out.write("</table>\n");
	}

	/**
	 * Prints content below the related pages area on the left.
	 */
	public void printBelowRelatedPages(HttpServletRequest req, Document document) throws JspException, IOException {
	}

	/**
	 * Begins a popup group.
	 *
	 * @see  #defaultBeginPopupGroup(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.html.Document, long)
	 */
	@Override
	public void beginPopupGroup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId) throws JspException, IOException {
		defaultBeginPopupGroup(req, resp, document, groupId);
	}

	/**
	 * Default implementation of beginPopupGroup.
	 */
	public static void defaultBeginPopupGroup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId) throws JspException, IOException {
		String groupIdStr = Long.toString(groupId);
		document.script().out(script -> script
			.append("  var popupGroupTimer").append(groupIdStr).append("=null;\n"
			+ "  var popupGroupAuto").append(groupIdStr).append("=null;\n"
			+ "  function popupGroupHideAllDetails").append(groupIdStr).append("() {\n"
			+ "    var spanElements = document.getElementsByTagName ? document.getElementsByTagName(\"div\") : document.all.tags(\"div\");\n"
			+ "    for (var c=0; c < spanElements.length; c++) {\n"
			+ "      if(spanElements[c].id.indexOf(\"aoPopup_").append(groupIdStr).append("_\")==0) {\n"
			+ "        spanElements[c].style.visibility=\"hidden\";\n"
			+ "      }\n"
			+ "    }\n"
			+ "  }\n"
			+ "  function popupGroupToggleDetails").append(groupIdStr).append("(popupId) {\n"
			+ "    if(popupGroupTimer").append(groupIdStr).append("!=null) clearTimeout(popupGroupTimer").append(groupIdStr).append(");\n"
			+ "    var elemStyle = document.getElementById(\"aoPopup_").append(groupIdStr).append("_\"+popupId).style;\n"
			+ "    if(elemStyle.visibility==\"visible\") {\n"
			+ "      elemStyle.visibility=\"hidden\";\n"
			+ "    } else {\n"
			+ "      popupGroupHideAllDetails").append(groupIdStr).append("();\n"
			+ "      elemStyle.visibility=\"visible\";\n"
			+ "    }\n"
			+ "  }\n"
			+ "  function popupGroupShowDetails").append(groupIdStr).append("(popupId) {\n"
			+ "    if(popupGroupTimer").append(groupIdStr).append("!=null) clearTimeout(popupGroupTimer").append(groupIdStr).append(");\n"
			+ "    var elemStyle = document.getElementById(\"aoPopup_").append(groupIdStr).append("_\"+popupId).style;\n"
			+ "    if(elemStyle.visibility!=\"visible\") {\n"
			+ "      popupGroupHideAllDetails").append(groupIdStr).append("();\n"
			+ "      elemStyle.visibility=\"visible\";\n"
			+ "    }\n"
			+ "  }\n")
		).__().nl();
	}

	/**
	 * Ends a popup group.
	 *
	 * @see  #defaultEndPopupGroup(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.html.Document, long)
	 */
	@Override
	public void endPopupGroup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId) throws JspException, IOException {
		defaultEndPopupGroup(req, resp, document, groupId);
	}

	/**
	 * Default implementation of endPopupGroup.
	 */
	public static void defaultEndPopupGroup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId) throws JspException, IOException {
		// Nothing at the popup group end
	}

	/**
	 * Begins a popup that is in a popup group.
	 *
	 * @see  #defaultBeginPopup(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.html.Document, long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void beginPopup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId, long popupId, String width) throws JspException, IOException {
		defaultBeginPopup(req, resp, document, groupId, popupId, width, getUrlBase(req));
	}

	/**
	 * Default implementation of beginPopup.
	 */
	public static void defaultBeginPopup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId, long popupId, String width, String urlBase) throws JspException, IOException {
		if(groupId < 0) throw new IllegalArgumentException("groupId < 0: " + groupId);
		final String groupIdStr = Long.toString(groupId);

		if(popupId < 0) throw new IllegalArgumentException("popupId < 0: " + popupId);
		final String popupIdStr = Long.toString(popupId);

		width = trimNullIfEmpty(width);
		ServletContext servletContext = req.getServletContext();
		Locale locale = LocaleAction.getLocale(servletContext, req);
		MessageResources applicationResources = getMessageResources(req);

		document.out.append("<div id=\"aoPopupAnchor_").append(groupIdStr).append('_').append(popupIdStr).append("\" class=\"aoPopupAnchor\">");
		document.img()
			.clazz("aoPopupAnchorImg")
			.src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase
						+ applicationResources.getMessage(locale, "TextSkin.popup.src")
					)
				)
			).alt(applicationResources.getMessage(locale, "TextSkin.popup.alt"))
			.width(Integer.parseInt(applicationResources.getMessage(locale, "TextSkin.popup.width")))
			.height(Integer.parseInt(applicationResources.getMessage(locale, "TextSkin.popup.height")))
			.onmouseover(onmouseover -> onmouseover
				.append("popupGroupTimer")
				.append(groupIdStr)
				.append("=setTimeout(\"popupGroupAuto")
				.append(groupIdStr)
				.append("=true; popupGroupShowDetails")
				.append(groupIdStr)
				.append('(')
				.append(popupIdStr)
				.append(")\", 1000);")
			).onmouseout(onmouseout -> onmouseout
				.append("if(popupGroupAuto")
				.append(groupIdStr)
				.append(") popupGroupHideAllDetails")
				.append(groupIdStr)
				.append("(); if(popupGroupTimer")
				.append(groupIdStr)
				.append("!=null) clearTimeout(popupGroupTimer")
				.append(groupIdStr)
				.append(");")
			).onclick(onclick -> onclick
				.append("popupGroupAuto")
				.append(groupIdStr)
				.append("=false; popupGroupToggleDetails")
				.append(groupIdStr)
				.append('(')
				.append(popupIdStr)
				.append(");")
			).__().nl()
		// Used to be span width=\"100%\"
		.out.append("    <div id=\"aoPopup_").append(groupIdStr).append('_').append(popupIdStr).append("\" class=\"aoPopupMain\"");
		if(width != null) {
			document.out.write(" style=\"");
			appendWidthStyle(width, document.out);
			document.out.write('"');
		}
		document.out.write(">\n"
		+ "        <table class=\"aoPopupTable ao-packed\">\n"
		+ "            <tr>\n"
		+ "                <td class=\"aoPopupTL\">");
		document.img()
			.src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase + "textskin/popup_topleft.gif"
					)
				)
			).width(12).height(12).alt("").__()
		.out.write("</td>\n"
		+ "                <td class=\"aoPopupTop\" style=\"background-image:url(");
		encodeTextInXhtmlAttribute(
			resp.encodeURL(
				URIEncoder.encodeURI(
					urlBase + "textskin/popup_top.gif"
				)
			),
			document.out
		);
		document.out.write(");\"></td>\n"
		+ "                <td class=\"aoPopupTR\">");
		document.img()
			.src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase + "textskin/popup_topright.gif"
					)
				)
			).width(12).height(12).alt("").__()
		.out.write("</td>\n"
		+ "            </tr>\n"
		+ "            <tr>\n"
		+ "                <td class=\"aoPopupLeft\" style=\"background-image:url(");
		encodeTextInXhtmlAttribute(
			resp.encodeURL(
				URIEncoder.encodeURI(
					urlBase + "textskin/popup_left.gif"
				)
			),
			document.out
		);
		document.out.write(");\"></td>\n"
		+ "                <td class=\"aoPopupLightRow\">");
	}

	/**
	 * Prints a popup close link/image/button for a popup that is part of a popup group.
	 *
	 * @see  #defaultPrintPopupClose(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.html.Document, long, long, java.lang.String)
	 */
	@Override
	public void printPopupClose(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId, long popupId) throws JspException, IOException {
		defaultPrintPopupClose(req, resp, document, groupId, popupId, getUrlBase(req));
	}

	/**
	 * Default implementation of printPopupClose.
	 */
	public static void defaultPrintPopupClose(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId, long popupId, String urlBase) throws JspException, IOException {
		ServletContext servletContext = req.getServletContext();
		Locale locale = LocaleAction.getLocale(servletContext, req);
		MessageResources applicationResources = getMessageResources(req);

		document.img()
			.clazz("aoPopupClose")
			.src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase + applicationResources.getMessage(locale, "TextSkin.popupClose.src")
					)
				)
			).alt(applicationResources.getMessage(locale, "TextSkin.popupClose.alt"))
			.width(Integer.parseInt(applicationResources.getMessage(locale, "TextSkin.popupClose.width")))
			.height(Integer.parseInt(applicationResources.getMessage(locale, "TextSkin.popupClose.height")))
			.onclick("popupGroupHideAllDetails" + groupId + "();")
		.__();
	}

	/**
	 * Ends a popup that is in a popup group.
	 *
	 * @see  #defaultEndPopup(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoindustries.html.Document, long, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void endPopup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId, long popupId, String width) throws JspException, IOException {
		TextSkin.defaultEndPopup(req, resp, document, groupId, popupId, width, getUrlBase(req));
	}

	/**
	 * Default implementation of endPopup.
	 */
	public static void defaultEndPopup(HttpServletRequest req, HttpServletResponse resp, Document document, long groupId, long popupId, String width, String urlBase) throws JspException, IOException {
		document.out.write("</td>\n"
		+ "                <td class=\"aoPopupRight\" style=\"background-image:url(");
		encodeTextInXhtmlAttribute(
			resp.encodeURL(
				URIEncoder.encodeURI(
					urlBase + "textskin/popup_right.gif"
				)
			),
			document.out
		);
		document.out.write(");\"></td>\n"
		+ "            </tr>\n"
		+ "            <tr>\n" 
		+ "                <td class=\"aoPopupBL\">");
		document.img()
			.src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase + "textskin/popup_bottomleft.gif"
					)
				)
			).width(12).height(12).alt("").__()
		.out.write("</td>\n"
		+ "                <td class=\"aoPopupBottom\" style=\"background-image:url(");
		encodeTextInXhtmlAttribute(
			resp.encodeURL(
				URIEncoder.encodeURI(
					urlBase + "textskin/popup_bottom.gif"
				)
			),
			document.out
		);
		document.out.write(");\"></td>\n"
		+ "                <td class=\"aoPopupBR\">");
		document.img()
			.src(
				resp.encodeURL(
					URIEncoder.encodeURI(
						urlBase + "textskin/popup_bottomright.gif"
					)
				)
			).width(12).height(12).alt("").__()
		.out.write("</td>\n"
		+ "            </tr>\n"
		+ "        </table>\n"
		+ "    </div>\n"
		+ "</div>\n");
		String groupIdStr = Long.toString(groupId);
		String popupIdStr = Long.toString(popupId);
		document.script().out(script -> script.append(
			  "\t// Override onload\n"
			+ "\tvar aoPopupOldOnload_").append(groupIdStr).append('_').append(popupIdStr).append(" = window.onload;\n"
			+ "\tfunction adjustPositionOnload_").append(groupIdStr).append('_').append(popupIdStr).append("() {\n"
			+ "\t\tadjustPosition_").append(groupIdStr).append('_').append(popupIdStr).append("();\n"
			+ "\t\tif(aoPopupOldOnload_").append(groupIdStr).append('_').append(popupIdStr).append(") {\n"
			+ "\t\t\taoPopupOldOnload_").append(groupIdStr).append('_').append(popupIdStr).append("();\n"
			+ "\t\t\taoPopupOldOnload_").append(groupIdStr).append('_').append(popupIdStr).append(" = null;\n"
			+ "\t\t}\n"
			+ "\t}\n"
			+ "\twindow.onload = adjustPositionOnload_").append(groupIdStr).append('_').append(popupIdStr).append(";\n"
			+ "\t// Override onresize\n"
			+ "\tvar aoPopupOldOnresize_").append(groupIdStr).append('_').append(popupIdStr).append(" = window.onresize;\n"
			+ "\tfunction adjustPositionOnresize_").append(groupIdStr).append('_').append(popupIdStr).append("() {\n"
			+ "\t\tadjustPosition_").append(groupIdStr).append('_').append(popupIdStr).append("();\n"
			+ "\t\tif(aoPopupOldOnresize_").append(groupIdStr).append('_').append(popupIdStr).append(") {\n"
			+ "\t\t\taoPopupOldOnresize_").append(groupIdStr).append('_').append(popupIdStr).append("();\n"
			+ "\t\t}\n"
			+ "\t}\n"
			+ "\twindow.onresize = adjustPositionOnresize_").append(groupIdStr).append('_').append(popupIdStr).append(";\n"
			+ "\tfunction adjustPosition_").append(groupIdStr).append('_').append(popupIdStr).append("() {\n"
			+ "\t\tvar popupAnchor = document.getElementById(\"aoPopupAnchor_").append(groupIdStr).append('_').append(popupIdStr).append("\");\n"
			+ "\t\tvar popup = document.getElementById(\"aoPopup_").append(groupIdStr).append('_').append(popupIdStr).append("\");\n"
			+ "\t\t// Find the screen position of the anchor\n"
			+ "\t\tvar popupAnchorLeft = 0;\n"
			+ "\t\tvar obj = popupAnchor;\n"
			+ "\t\tif(obj.offsetParent) {\n"
			+ "\t\t\tpopupAnchorLeft = obj.offsetLeft\n"
			+ "\t\t\twhile (obj = obj.offsetParent) {\n"
			+ "\t\t\t\tpopupAnchorLeft += obj.offsetLeft\n"
			+ "\t\t\t}\n"
			+ "\t\t}\n"
			+ "\t\tvar popupAnchorRight = popupAnchorLeft + popupAnchor.offsetWidth;\n"
			+ "\t\t// Find the width of the popup\n"
			+ "\t\tvar popupWidth = popup.offsetWidth;\n"
			+ "\t\t// Find the width of the screen\n"
			+ "\t\tvar screenWidth = (document.compatMode && document.compatMode == \"CSS1Compat\") ? document.documentElement.clientWidth : document.body.clientWidth;\n"
			+ "\t\t// Find the desired screen position of the popup\n"
			+ "\t\tvar popupScreenPosition = 0;\n"
			+ "\t\tif(screenWidth<=(popupWidth+12)) {\n"
			+ "\t\t\tpopupScreenPosition = 0;\n"
			+ "\t\t} else {\n"
			+ "\t\t\tpopupScreenPosition = screenWidth - popupWidth - 12;\n"
			+ "\t\t\tif(popupAnchorRight < popupScreenPosition) popupScreenPosition = popupAnchorRight;\n"
			+ "\t\t}\n"
			+ "\t\tpopup.style.left=(popupScreenPosition-popupAnchorLeft)+\"px\";\n"
			+ "\t}\n"
			+ "\t// Call once at parse time for when the popup is activated while page loading (before onload called)\n"
			+ "\tadjustPosition_").append(groupIdStr).append('_').append(popupIdStr).append("();\n"
		)).__();
	}
}
