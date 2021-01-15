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
package com.aoindustries.website;

import com.aoindustries.exception.WrappedException;
import com.aoindustries.net.IRI;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.net.URIParametersUtils;
import com.aoindustries.net.URIParser;
import com.aoindustries.servlet.http.Canonical;
import com.aoindustries.servlet.http.HttpServletUtil;
import com.aoindustries.tempfiles.servlet.TempFileContextEE;
import com.aoindustries.web.resources.servlet.RegistryEE;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import org.apache.struts.Globals;

/**
 * @author  AO Industries, Inc.
 */
public class SessionResponseWrapper extends HttpServletResponseWrapper {

	private static final Logger logger = Logger.getLogger(SessionResponseWrapper.class.getName());

	private static final String ROBOTS_HEADER_NAME = "X-Robots-Tag";
	private static final String ROBOTS_HEADER_VALUE = "noindex, nofollow";

	final private HttpServletRequest request;
	final private HttpServletResponse response;

	public SessionResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
		super(response);
		this.request = request;
		this.response = response;
		// When the request has any "authenticationTarget" parameter, set noindex headers
		if(request.getParameter(Constants.AUTHENTICATION_TARGET) != null) {
			if(!response.containsHeader(ROBOTS_HEADER_NAME)) {
				response.setHeader(ROBOTS_HEADER_NAME, ROBOTS_HEADER_VALUE);
			}
		}
	}

	private String encode(String url, boolean isRedirect) {
		// Don't rewrite empty or anchor-only URLs
		if(url.isEmpty() || url.charAt(0) == '#') return url;
		try {
			boolean canonical = Canonical.get();
			SiteSettings siteSettings = SiteSettings.getInstance(request.getServletContext());
			List<Skin.Language> languages = siteSettings.getLanguages(request);
			// Short-cut canonical URL processing when there are not multiple languages
			if(canonical && languages.size() <= 1) {
				// Canonical URLs may only include the language, and there is not multiple
				// languages, leave URL unaltered:
				return url;
			}
			// If starts with http:// or https:// parse out the first part of the URL, encode the path, and reassemble.
			String protocol;
			String remaining;
			if(
				// 7: "http://".length()
				url.length() > 7
				&& url.charAt(5) == '/'
				&& url.charAt(6) == '/'
				&& URIParser.isScheme(url, "http")
			) {
				protocol = url.substring(0, 7);
				remaining = url.substring(7);
			} else if(
				// 8: "https://".length()
				url.length() > 8
				&& url.charAt(6) == '/'
				&& url.charAt(7) == '/'
				&& URIParser.isScheme(url, "https")
			) {
				protocol = url.substring(0, 8);
				remaining = url.substring(8);
			} else if(
				URIParser.isScheme(url, "javascript")
				|| URIParser.isScheme(url, "mailto")
				|| URIParser.isScheme(url, "telnet")
				|| URIParser.isScheme(url, "tel")
				|| URIParser.isScheme(url, "cid")
				|| URIParser.isScheme(url, "file")
				|| URIParser.isScheme(url, "data")
			) {
				return url;
			} else {
				return addNoCookieParameters(canonical, siteSettings, languages, url, isRedirect);
			}
			int slashPos = remaining.indexOf('/');
			if(slashPos == -1) slashPos = remaining.length();
			String hostPort = remaining.substring(0, slashPos);
			int colonPos = hostPort.indexOf(':');
			String host = colonPos == -1 ? hostPort : hostPort.substring(0, colonPos);
			String encoded;
			if(
				// TODO: What about [...] IPv6 addresses?
				host.equalsIgnoreCase(request.getServerName())
			) {
				String withCookies = addNoCookieParameters(canonical, siteSettings, languages, remaining.substring(slashPos), isRedirect);
				int newUrlLen = protocol.length() + hostPort.length() + withCookies.length();
				if(newUrlLen == url.length()) {
					assert url.equals(protocol + hostPort + withCookies);
					encoded = url;
				} else {
					StringBuilder newUrl = new StringBuilder(newUrlLen);
					newUrl.append(protocol).append(hostPort).append(withCookies);
					assert newUrl.length() == newUrlLen;
					encoded = newUrl.toString();
				}
			} else {
				// Going to an different hostname, do not add request parameters
				encoded = url;
			}
			return encoded;
		} catch(JspException | IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	/**
	 * @deprecated  Please use encodeURL.
	 *
	 * @see  #encodeURL(String)
	 */
	@Deprecated
	@Override
	public String encodeUrl(String url) {
		return encode(url, false);
	}

	@Override
	public String encodeURL(String url) {
		return encode(url, false);
	}

	/**
	 * @deprecated  Please use encodeRedirectURL.
	 *
	 * @see  #encodeRedirectURL(String)
	 */
	@Deprecated
	@Override
	public String encodeRedirectUrl(String url) {
		return encode(url, true);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return encode(url, true);
	}

	/**
	 * Adds the no cookie parameters (language and layout) if needed and not already set.
	 */
	private String addNoCookieParameters(boolean canonical, SiteSettings siteSettings, List<Skin.Language> languages, String url, boolean isRedirect) throws JspException, IOException, SQLException {
		HttpSession session = request.getSession(false);
		if(canonical || session == null || session.isNew() || request.isRequestedSessionIdFromURL()) {
			IRI iri = new IRI(url);
			// Don't add for certains file types
			// TODO: This list is getting long.  Use a map?
			if(
				// Matches LocaleFilter.java
				// Matches NoSessionFilter.java
				// Is SessionResponseWrapper.java
				// Related to LastModifiedServlet.java
				// Related to ao-mime-types/…/web-fragment.xml
				// Related to ContentType.java
				// Related to MimeType.java
				!iri.pathEndsWithIgnoreCase(".bmp")
				&& !iri.pathEndsWithIgnoreCase(".css")
				&& !iri.pathEndsWithIgnoreCase(".dia")
				&& !iri.pathEndsWithIgnoreCase(".exe")
				&& !iri.pathEndsWithIgnoreCase(".gif")
				&& !iri.pathEndsWithIgnoreCase(".ico")
				&& !iri.pathEndsWithIgnoreCase(".jpeg")
				&& !iri.pathEndsWithIgnoreCase(".jpg")
				&& !iri.pathEndsWithIgnoreCase(".js")
				&& !iri.pathEndsWithIgnoreCase(".png")
				&& !iri.pathEndsWithIgnoreCase(".svg")
				&& !iri.pathEndsWithIgnoreCase(".txt")
				&& !iri.pathEndsWithIgnoreCase(".webp")
				&& !iri.pathEndsWithIgnoreCase(".zip")
				// Web development
				&& !iri.pathEndsWithIgnoreCase(".less")
				&& !iri.pathEndsWithIgnoreCase(".sass")
				&& !iri.pathEndsWithIgnoreCase(".scss")
				&& !iri.pathEndsWithIgnoreCase(".css.map")
				&& !iri.pathEndsWithIgnoreCase(".js.map")
			) {
				if(!canonical && session != null) {
					// Use the default servlet container jsessionid when any session object exists besides
					// the three values that will be encoded into the URL as parameters below.
					Enumeration<String> attributeNames = session.getAttributeNames();
					String whyNeedsJsessionid = null;
					while(attributeNames.hasMoreElements()) {
						String name = attributeNames.nextElement();
						if(
							!Constants.AUTHENTICATION_TARGET.equals(name)
							&& !Globals.LOCALE_KEY.equals(name)
							&& !Constants.LAYOUT.equals(name)
							&& !Constants.SU_REQUESTED.equals(name)
							// JSTL 1.1
							&& !"javax.servlet.jsp.jstl.fmt.request.charset".equals(name) // TODO: Use constants from somewhere
							&& !"javax.servlet.jsp.jstl.fmt.locale.session".equals(name)  // TODO: Use constants from somewhere
							// Allow session-based temporary file context
							&& !TempFileContextEE.SESSION_ATTRIBUTE.equals(name)
							// Allow session-based web resource registry
							&& !RegistryEE.Session.SESSION_ATTRIBUTE.equals(name)
						) {
							// These will always trigger jsessionid
							if(
								Constants.AO_CONN.equals(name)
								|| Constants.AUTHENTICATED_AO_CONN.equals(name)
							) {
								whyNeedsJsessionid = name;
								break;
							}
							// Must be an SessionActionForm if none of the above
							Object sessionObject = session.getAttribute(name);
							if(sessionObject instanceof SessionActionForm) {
								SessionActionForm sessionActionForm = (SessionActionForm)sessionObject;
								if(!sessionActionForm.isEmpty()) {
									whyNeedsJsessionid = name;
									break;
								}
							} else {
								Class<?> clazz = (sessionObject == null) ? null : sessionObject.getClass();
								throw new AssertionError("Session object is neither an expected value nor a SessionActionForm.  name="+name+", sessionObject.class="+(clazz == null ? null : clazz.getName()));
							}
						}
					}
					if(whyNeedsJsessionid!=null) {
						if(HttpServletUtil.isGooglebot(request)) {
							// Create or update a ticket about the problem
							logger.logp(
								Level.WARNING,
								SessionResponseWrapper.class.getName(),
								"addNoCookieParameters",
								"Refusing to send jsessionid to Googlebot eventhough request would normally need jsessionid.  Other search engines may be affected.  Reason: "+whyNeedsJsessionid
							);
						} else {
							// System.out.println("DEBUG: Why needs jsessionid: "+whyNeedsJsessionid);
							return isRedirect ? response.encodeRedirectURL(url) : response.encodeURL(url);
						}
					}
				}

				URIParameters splitURIParameters = null;
				MutableURIParameters cookieParams = null;

				if(!canonical) {
					// Add the Constants.AUTHENTICATION_TARGET if needed
					String authenticationTarget = (session == null) ? null : (String)session.getAttribute(Constants.AUTHENTICATION_TARGET);
					if(authenticationTarget==null) authenticationTarget = request.getParameter(Constants.AUTHENTICATION_TARGET);
					//System.err.println("DEBUG: addNoCookieParameters: authenticationTarget="+authenticationTarget);
					if(authenticationTarget != null) {
						if(splitURIParameters == null) splitURIParameters = URIParametersUtils.of(iri.getQueryString());
						if(!splitURIParameters.getParameterMap().containsKey(Constants.AUTHENTICATION_TARGET)) {
							if(cookieParams == null) cookieParams = new URIParametersMap();
							cookieParams.addParameter(Constants.AUTHENTICATION_TARGET, authenticationTarget);
						}
					}
				}

				if(session != null) {
					// Only add the language if there is more than one possibility
					if(languages.size()>1) {
						Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
						if(locale!=null) {
							String code = locale.getLanguage();
							// Don't add if is the default language
							Locale defaultLocale = LocaleAction.getDefaultLocale(siteSettings, request);
							if(!code.equals(defaultLocale.getLanguage())) {
								for(Skin.Language language : languages) {
									if(language.getCode().equals(code)) {
										if(splitURIParameters == null) splitURIParameters = URIParametersUtils.of(iri.getQueryString());
										if(!splitURIParameters.getParameterMap().containsKey("language")) {
											if(cookieParams == null) cookieParams = new URIParametersMap();
											cookieParams.addParameter("language", code);
										}
										break;
									}
								}
							}
						}
					}
					if(!canonical) {
						// Only add the layout if there is more than one possibility
						List<Skin> skins = siteSettings.getSkins();
						if(skins.size()>1) {
							String layout = (String)session.getAttribute(Constants.LAYOUT);
							if(layout!=null) {
								// Don't add if is the default layout
								Skin defaultSkin = SkinAction.getDefaultSkin(skins, request);
								if(!layout.equals(defaultSkin.getName())) {
									// Make sure it is one of the allowed skins
									for(Skin skin : skins) {
										if(skin.getName().equals(layout)) {
											if(splitURIParameters == null) splitURIParameters = URIParametersUtils.of(iri.getQueryString());
											if(!splitURIParameters.getParameterMap().containsKey("layout")) {
												if(cookieParams == null) cookieParams = new URIParametersMap();
												cookieParams.addParameter("layout", layout);
											}
											break;
										}
									}
								}
							}
						}
						// Add any "su"
						String su = (String)session.getAttribute(Constants.SU_REQUESTED);
						if(su != null && !su.isEmpty()) {
							if(splitURIParameters == null) splitURIParameters = URIParametersUtils.of(iri.getQueryString());
							if(!splitURIParameters.getParameterMap().containsKey("su")) {
								if(cookieParams == null) cookieParams = new URIParametersMap();
								cookieParams.addParameter("su", su);
							}
						}
					}
				}
				url = iri.addParameters(cookieParams).toASCIIString();
			} else {
				//System.err.println("DEBUG: addNoCookieParameters: Not adding parameters to skipped type: "+url);
			}
		}
		return url;
	}
}
