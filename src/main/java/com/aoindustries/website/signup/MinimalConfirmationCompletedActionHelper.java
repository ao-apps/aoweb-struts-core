/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009-2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.reseller.Brand;
import com.aoindustries.encoding.Doctype;
import com.aoindustries.encoding.EncodingContext;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.encoding.Serialization;
import com.aoindustries.html.Document;
import com.aoindustries.html.META;
import com.aoindustries.html.SCRIPT;
import com.aoindustries.html.STYLE;
import com.aoindustries.io.FindReplaceWriter;
import com.aoindustries.io.IoUtils;
import com.aoindustries.io.NativeToUnixWriter;
import com.aoindustries.net.HostAddress;
import com.aoindustries.taglib.GlobalAttributes;
import com.aoindustries.taglib.HtmlTag;
import com.aoindustries.util.i18n.ThreadLocale;
import com.aoindustries.website.Mailer;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.TextSkin;
import static com.aoindustries.website.signup.Resources.PACKAGE_RESOURCES;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionServlet;

/**
 * @author  AO Industries, Inc.
 */
final public class MinimalConfirmationCompletedActionHelper {

	/**
	 * Make no instances.
	 */
	private MinimalConfirmationCompletedActionHelper() {}

	// TODO: Have this generate a ticket instead, with full details.  Remove "all except bank card numbers" in other places once done.
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	public static void sendSupportSummaryEmail(
		ActionServlet servlet,
		HttpServletRequest request,
		String pkey,
		String statusKey,
		SiteSettings siteSettings,
		PackageDefinition packageDefinition,
		SignupOrganizationForm signupOrganizationForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) {
		try {
			sendSummaryEmail(servlet, pkey, statusKey, siteSettings.getBrand().getAowebStrutsSignupAdminAddress(), siteSettings, packageDefinition, signupOrganizationForm, signupTechnicalForm, signupBillingInformationForm);
		} catch(ThreadDeath td) {
			throw td;
		} catch(Throwable t) {
			servlet.log("Unable to send sign up details to support admin address", t);
		}
	}

	/**
	 * Sends the customer emails and stores the successAddresses and failureAddresses as request attributes.
	 */
	public static void sendCustomerSummaryEmails(
		ActionServlet servlet,
		HttpServletRequest request,
		String pkey,
		String statusKey,
		SiteSettings siteSettings,
		PackageDefinition packageDefinition,
		SignupOrganizationForm signupOrganizationForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) {
		Set<String> addresses = new HashSet<>();
		addresses.add(signupTechnicalForm.getBaEmail());
		addresses.add(signupBillingInformationForm.getBillingEmail());
		Set<String> successAddresses = new HashSet<>();
		Set<String> failureAddresses = new HashSet<>();
		Iterator<String> I=addresses.iterator();
		while(I.hasNext()) {
			String address=I.next();
			boolean success = sendSummaryEmail(servlet, pkey, statusKey, address, siteSettings, packageDefinition, signupOrganizationForm, signupTechnicalForm, signupBillingInformationForm);
			if(success) successAddresses.add(address);
			else failureAddresses.add(address);
		}

		// Store request attributes
		request.setAttribute("successAddresses", successAddresses);
		request.setAttribute("failureAddresses", failureAddresses);
	}

	/**
	 * Sends a summary email and returns <code>true</code> if successful.
	 */
	@SuppressWarnings({"deprecation", "UseSpecificCatch", "TooBroadCatch"})
	private static boolean sendSummaryEmail(
		ActionServlet servlet,
		String pkey,
		String statusKey,
		String recipient,
		SiteSettings siteSettings,
		PackageDefinition packageDefinition,
		SignupOrganizationForm signupOrganizationForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) {
		try {
			String subject = PACKAGE_RESOURCES.getMessage("serverConfirmationCompleted.email.subject", pkey);
	
			// Find the locale and related resource bundles
			Locale userLocale = ThreadLocale.get();
			String charset = Document.ENCODING.name(); // TODO: US-ASCII with automatic entity encoding

			// Generate the email contents
			// TODO: Test emails
			StringWriter buffer = new StringWriter();
			Document document = new Document(
				new EncodingContext() {
					@Override
					public Serialization getSerialization() {
						return Serialization.SGML;
					}
					@Override
					public Doctype getDoctype() {
						return Doctype.STRICT;
					}
				},
				NativeToUnixWriter.getInstance(new FindReplaceWriter(buffer, "\n", "\r\n"))
			);
			document.setIndent(true);
			document.xmlDeclaration(charset);
			document.doctype();
			HtmlTag.beginHtmlTag(userLocale, document.out, document.serialization, (GlobalAttributes)null); document.out.write("\n"
			+ "<head>\n");
			String contentType = document.serialization.getContentType() + "; charset=" + charset;
			if(document.doctype == Doctype.HTML5) {
				document.out.write("    "); document.meta().charset(charset).__().nl();
			} else {
				document.out.write("    "); document.meta(META.HttpEquiv.CONTENT_TYPE).content(contentType).__().out.write("\n"
				// Default style language
				+ "    "); document.meta(META.HttpEquiv.CONTENT_STYLE_TYPE).content(STYLE.Type.TEXT_CSS).__().out.write("\n"
				+ "    "); document.meta(META.HttpEquiv.CONTENT_SCRIPT_TYPE).content(SCRIPT.Type.TEXT_JAVASCRIPT).__().nl();
			}
			document.out.write("    <title>"); document.text(subject).out.write("</title>\n");
			// Embed the text-only style sheet
			InputStream cssIn = servlet.getServletContext().getResourceAsStream(TextSkin.TEXTSKIN_CSS.getUri());
			if(cssIn != null) {
				try {
					document.out.write("    "); try (MediaWriter style = document.style().out__()) {
						Reader cssReader = new InputStreamReader(cssIn);
						try {
							IoUtils.copy(cssReader, style);
						} finally {
							cssIn.close();
						}
					} document.nl();
				} finally {
					cssIn.close();
				}
			} else {
				servlet.log("Warning: Unable to find resource: " + TextSkin.TEXTSKIN_CSS);
			}
			document.out.append("</head>\n"
			+ "<body>\n"
			+ "<table style=\"border:0px\" cellpadding=\"0\" cellspacing=\"0\">\n"
			+ "    <tr><td style=\"white-space:nowrap\" colspan=\"3\">\n"
			+ "        ").append(PACKAGE_RESOURCES.getMessage(statusKey, pkey)); document.br__().out.write("\n"
			+ "        "); document.br__().out.append("\n"
			+ "        ").append(PACKAGE_RESOURCES.getMessage("serverConfirmationCompleted.belowIsSummary")); document.br__().out.write("\n"
			+ "        "); document.hr__(); document.out.write("\n"
			+ "    </td></tr>\n"
			+ "    <tr><th colspan=\"3\">"); document.text(PACKAGE_RESOURCES.getMessage("steps.selectPackage.label")); document.out.write("</th></tr>\n");
			SignupSelectPackageActionHelper.writeEmailConfirmation(document, packageDefinition);
			AOServConnector rootConn = siteSettings.getRootAOServConnector();
			document.out.write("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
			+ "    <tr><th colspan=\"3\">"); document.text(PACKAGE_RESOURCES.getMessage("steps.organizationInfo.label")); document.out.write("</th></tr>\n");
			SignupOrganizationActionHelper.writeEmailConfirmation(document, rootConn, signupOrganizationForm);
			document.out.write("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
			+ "    <tr><th colspan=\"3\">"); document.text(PACKAGE_RESOURCES.getMessage("steps.technicalInfo.label")); document.out.write("</th></tr>\n");
			SignupTechnicalActionHelper.writeEmailConfirmation(document, rootConn, signupTechnicalForm);
			document.out.write("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
			+ "    <tr><th colspan=\"3\">"); document.text(PACKAGE_RESOURCES.getMessage("steps.billingInformation.label")); document.out.write("</th></tr>\n");
			SignupBillingInformationActionHelper.writeEmailConfirmation(document, signupBillingInformationForm);
			document.out.write("</table>\n"
			+ "</body>\n");
			HtmlTag.endHtmlTag(document.out); document.nl();

			// Send the email
			Brand brand = siteSettings.getBrand();
			Mailer.sendEmail(HostAddress.valueOf(brand.getSignupEmailAddress().getDomain().getLinuxServer().getHostname()),
				contentType,
				charset,
				brand.getSignupEmailAddress().toString(),
				brand.getSignupEmailDisplay(),
				Collections.singletonList(recipient),
				subject,
				buffer.toString()
			);

			return true;
		} catch(ThreadDeath td) {
			throw td;
		} catch(Throwable t) {
			servlet.log("Unable to send sign up details to "+recipient, t);
			return false;
		}
	}
}
