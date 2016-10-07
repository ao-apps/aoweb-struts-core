/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
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

	private static Logger getLogger(ServletContext servletContext) {
		return LogFactory.getLogger(servletContext, SessionResponseWrapper.class);
	}

	final private HttpServletRequest request;
	final private HttpServletResponse response;

	public SessionResponseWrapper(HttpServletRequest request, HttpServletResponse response) {
		super(response);
		this.request = request;
		this.response = response;
	}

	/**
	 * @deprecated  Please use encodeURL.
	 *
	 * @see  #encodeURL(String)
	 */
	@Deprecated
	@Override
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public String encodeURL(final String url) {
		//System.err.println("DEBUG: encodeURL: "+url);
		try {
			// If starts with http:// or https:// parse out the first part of the URL, encode the path, and reassemble.
			String protocol;
			String remaining;
			if(url.length()>7 && (protocol=url.substring(0, 7)).equalsIgnoreCase("http://")) {
				remaining = url.substring(7);
			} else if(url.length()>8 && (protocol=url.substring(0, 8)).equalsIgnoreCase("https://")) {
				remaining = url.substring(8);
			} else {
				return addNoCookieParameters(url, false);
				//return response.encodeURL(url);
			}
			int slashPos = remaining.indexOf('/');
			if(slashPos==-1) {
				return addNoCookieParameters(url, false);
				//return response.encodeURL(url);
			}
			String hostPort = remaining.substring(0, slashPos);
			int colonPos = hostPort.indexOf(':');
			String host = colonPos==-1 ? hostPort : hostPort.substring(0, colonPos);
			String encoded;
			if(host.equalsIgnoreCase(request.getServerName())) {
				encoded = protocol + hostPort + addNoCookieParameters(remaining.substring(slashPos), false);
				//encoded = protocol + hostPort + response.encodeURL(remaining.substring(slashPos));
			} else {
				// Going to an different hostname, do not add request parameters
				//System.err.println("DEBUG: encodeURL: Not adding parameters to external link: "+url);
				encoded = url;
				//encoded = response.encodeURL(url);
			}
			return encoded;
		} catch(JspException err) {
			throw new WrappedException(err);
		} catch(IOException err) {
			throw new WrappedException(err);
		} catch(SQLException err) {
			throw new WrappedException(err);
		}
	}

	/**
	 * @deprecated  Please use encodeRedirectURL.
	 *
	 * @see  #encodeRedirectURL(String)
	 */
	@Deprecated
	@Override
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		//System.err.println("DEBUG: encodeRedirectURL: "+url);
		try {
			// If starts with http:// or https:// parse out the first part of the URL, encode the path, and reassemble.
			String protocol;
			String remaining;
			if(url.length()>7 && (protocol=url.substring(0, 7)).equalsIgnoreCase("http://")) {
				remaining = url.substring(7);
			} else if(url.length()>8 && (protocol=url.substring(0, 8)).equalsIgnoreCase("https://")) {
				remaining = url.substring(8);
			} else {
				return addNoCookieParameters(url, true);
				//return response.encodeRedirectURL(url);
			}
			int slashPos = remaining.indexOf('/');
			if(slashPos==-1) {
				return addNoCookieParameters(url, true);
				//return response.encodeRedirectURL(url);
			}
			String hostPort = remaining.substring(0, slashPos);
			int colonPos = hostPort.indexOf(':');
			String host = colonPos==-1 ? hostPort : hostPort.substring(0, colonPos);
			String encoded;
			if(host.equalsIgnoreCase(request.getServerName())) {
				encoded = protocol + hostPort + addNoCookieParameters(remaining.substring(slashPos), true);
				//encoded = protocol + hostPort + response.encodeRedirectURL(remaining.substring(slashPos));
			} else {
				// Going to an different hostname, do not add request parameters
				//System.err.println("DEBUG: encodeRedirectURL: Not adding parameters to external link: "+url);
				encoded = url;
				//encoded = response.encodeRedirectURL(url);
			}
			return encoded;
		} catch(JspException err) {
			throw new WrappedException(err);
		} catch(IOException err) {
			throw new WrappedException(err);
		} catch(SQLException err) {
			throw new WrappedException(err);
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public void setStatus(int sc, String sm) {
		super.setStatus(sc, sm);
	}

	/**
	 * Adds the no cookie parameters (language and layout) if needed and not already set.
	 */
	private String addNoCookieParameters(String url, boolean isRedirect) throws JspException, UnsupportedEncodingException, IOException, SQLException {
		HttpSession session = request.getSession();
		if(session.isNew() || request.isRequestedSessionIdFromURL()) {
			// Don't add for certains file types
			int pos = url.indexOf('?');
			String lowerPath = (pos==-1 ? url : url.substring(0, pos)).toLowerCase(Locale.ENGLISH);
			//System.err.println("DEBUG: addNoCookieParameters: lowerPath="+lowerPath);
			if(
				!lowerPath.endsWith(".css")
				&& !lowerPath.endsWith(".gif")
				&& !lowerPath.endsWith(".ico")
				&& !lowerPath.endsWith(".jpeg")
				&& !lowerPath.endsWith(".jpg")
				&& !lowerPath.endsWith(".js")
				&& !lowerPath.endsWith(".png")
				&& !lowerPath.endsWith(".txt")
			) {
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
						&& !"javax.servlet.jsp.jstl.fmt.request.charset".equals(name)
						&& !"javax.servlet.jsp.jstl.fmt.locale.session".equals(name)
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
							throw new AssertionError("Session object is neither an expected value nor a SessionActionForm.  name="+name+", sessionObject.class="+sessionObject.getClass().getName());
						}
					}
				}
				ServletContext servletContext = session.getServletContext();
				if(whyNeedsJsessionid!=null) {
					if(ServletUtil.isGooglebot(request)) {
						// Create or update a ticket about the problem
						getLogger(servletContext).logp(
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

				// Add the Constants.AUTHENTICATION_TARGET if needed
				String authenticationTarget = (String)session.getAttribute(Constants.AUTHENTICATION_TARGET);
				if(authenticationTarget==null) authenticationTarget = request.getParameter(Constants.AUTHENTICATION_TARGET);
				//System.err.println("DEBUG: addNoCookieParameters: authenticationTarget="+authenticationTarget);
				if(authenticationTarget!=null) url = addParamIfMissing(url, Constants.AUTHENTICATION_TARGET, authenticationTarget, getCharacterEncoding());

				// Only add the language if there is more than one possibility
				SiteSettings siteSettings = SiteSettings.getInstance(servletContext);
				List<Skin.Language> languages = siteSettings.getLanguages(request);
				if(languages.size()>1) {
					Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
					if(locale!=null) {
						String code = locale.getLanguage();
						// Don't add if is the default language
						Locale defaultLocale = LocaleAction.getDefaultLocale(siteSettings, request);
						if(!code.equals(defaultLocale.getLanguage())) {
							for(Skin.Language language : languages) {
								if(language.getCode().equals(code)) {
									url = addParamIfMissing(url, "language", code, getCharacterEncoding());
									break;
								}
							}
						}
					}
				}
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
									url = addParamIfMissing(url, "layout", layout, getCharacterEncoding());
									break;
								}
							}
						}
					}
				}
				// Add any "su"
				String su = (String)session.getAttribute(Constants.SU_REQUESTED);
				if(su!=null) url = addParamIfMissing(url, "su", su, getCharacterEncoding());
			} else {
				//System.err.println("DEBUG: encodeRedirectURL: Not adding parameters to skipped type: "+url);
			}
		}
		return url;
	}

	/**
	 * Adds a parameter if missing.
	 */
	private static String addParamIfMissing(String url, String name, String value, String encoding) throws UnsupportedEncodingException {
		String encodedName = URLEncoder.encode(name, encoding);
		if(url.indexOf('?')==-1) {
			// No params exist, just add it
			return url+'?'+encodedName+'='+URLEncoder.encode(value, encoding);
		}
		if(
			!url.contains("?"+encodedName+'=')
			&& !url.contains("&"+encodedName+'=')
		) {
			return url+'&'+encodedName+'='+URLEncoder.encode(value, encoding);
		}
		return url;
	}
}
