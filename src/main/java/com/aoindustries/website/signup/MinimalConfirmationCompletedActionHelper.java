/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009-2013, 2015, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.encoding.Doctype;
import com.aoindustries.encoding.EncodingContext;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.encoding.Serialization;
import com.aoindustries.html.Html;
import com.aoindustries.html.Meta;
import com.aoindustries.io.IoUtils;
import com.aoindustries.net.HostAddress;
import com.aoindustries.taglib.HtmlTag;
import com.aoindustries.util.i18n.ThreadLocale;
import com.aoindustries.website.Mailer;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.TextSkin;
import static com.aoindustries.website.signup.ApplicationResources.accessor;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.mail.MessagingException;
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
			sendSummaryEmail(servlet, request, pkey, statusKey, siteSettings.getBrand().getAowebStrutsSignupAdminAddress(), siteSettings, packageDefinition, signupOrganizationForm, signupTechnicalForm, signupBillingInformationForm);
		} catch(RuntimeException | IOException | SQLException err) {
			servlet.log("Unable to send sign up details to support admin address", err);
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
			boolean success = sendSummaryEmail(servlet, request, pkey, statusKey, address, siteSettings, packageDefinition, signupOrganizationForm, signupTechnicalForm, signupBillingInformationForm);
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
	@SuppressWarnings("deprecation")
	private static boolean sendSummaryEmail(
		ActionServlet servlet,
		HttpServletRequest request,
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
			// Find the locale and related resource bundles
			Locale userLocale = ThreadLocale.get();
			String charset = Html.ENCODING.name(); // TODO: US-ASCII with automatic entity encoding

			// Generate the email contents
			// TODO: Test emails
			CharArrayWriter cout = new CharArrayWriter();
			EncodingContext encodingContext = new EncodingContext() {
				@Override
				public Serialization getSerialization() {
					return Serialization.SGML;
				}
				@Override
				public Doctype getDoctype() {
					return Doctype.STRICT;
				}
			};
			ChainWriter emailOut = new ChainWriter(encodingContext, cout);
			Html html = new Html(encodingContext, cout);
			html.xmlDeclaration(charset);
			html.doctype();
			HtmlTag.beginHtmlTag(userLocale, cout, html.serialization, charset);
			emailOut.print("\n"
						 + "<head>\n"
						 + "    ");
			html.meta(Meta.HttpEquiv.CONTENT_TYPE).content(content -> {
				content.write(html.serialization.getContentType());
				content.write("; charset=");
				content.write(charset);
			}).__().nl();
			// Embed the text-only style sheet
			InputStream cssIn = servlet.getServletContext().getResourceAsStream(TextSkin.TEXTSKIN_CSS.getUri());
			if(cssIn != null) {
				try {
					emailOut.print("    ");
					try (MediaWriter style = html.style().out__()) {
						Reader cssReader = new InputStreamReader(cssIn);
						try {
							IoUtils.copy(cssReader, style);
						} finally {
							cssIn.close();
						}
					}
					html.nl();
				} finally {
					cssIn.close();
				}
			} else {
				servlet.log("Warning: Unable to find resource: " + TextSkin.TEXTSKIN_CSS);
			}
			emailOut.print("</head>\n"
						 + "<body>\n"
						 + "<table style=\"border:0px\" cellpadding=\"0\" cellspacing=\"0\">\n"
						 + "    <tr><td style=\"white-space:nowrap\" colspan=\"3\">\n"
						 + "        ").print(accessor.getMessage(statusKey, pkey));
			html.br__().nl();
			emailOut.print("        ");
			html.br__().nl();
			emailOut.print("        ").print(accessor.getMessage("serverConfirmationCompleted.belowIsSummary"));
			html.br__().nl();
			emailOut.print("        ");
			html.hr__();
			emailOut.print("    </td></tr>\n"
						 + "    <tr><th colspan=\"3\">");
			html.text(accessor.getMessage("steps.selectPackage.label"));
			emailOut.print("</th></tr>\n");
			SignupSelectPackageActionHelper.printConfirmation(emailOut, html, packageDefinition);
			AOServConnector rootConn = siteSettings.getRootAOServConnector();
			emailOut.print("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
						 + "    <tr><th colspan=\"3\">");
			html.text(accessor.getMessage("steps.organizationInfo.label"));
			emailOut.print("</th></tr>\n");
			SignupOrganizationActionHelper.printConfirmation(emailOut, html, rootConn, signupOrganizationForm);
			emailOut.print("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
						 + "    <tr><th colspan=\"3\">");
			html.text(accessor.getMessage("steps.technicalInfo.label"));
			emailOut.print("</th></tr>\n");
			SignupTechnicalActionHelper.printConfirmation(emailOut, html, rootConn, signupTechnicalForm);
			emailOut.print("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
						 + "    <tr><th colspan=\"3\">");
			html.text(accessor.getMessage("steps.billingInformation.label"));
			emailOut.print("</th></tr>\n");
			SignupBillingInformationActionHelper.printConfirmation(emailOut, html, signupBillingInformationForm);
			emailOut.print("</table>\n"
						 + "</body>\n");
			HtmlTag.endHtmlTag(emailOut);
			emailOut.print('\n');
			emailOut.flush();

			// Send the email
			Brand brand = siteSettings.getBrand();
			Mailer.sendEmail(
				HostAddress.valueOf(brand.getSignupEmailAddress().getDomain().getLinuxServer().getHostname()),
				html.serialization.getContentType(),
				charset,
				brand.getSignupEmailAddress().toString(),
				brand.getSignupEmailDisplay(),
				Collections.singletonList(recipient),
				accessor.getMessage("serverConfirmationCompleted.email.subject", pkey),
				cout.toString()
			);

			return true;
		} catch(RuntimeException | IOException | SQLException | MessagingException err) {
			servlet.log("Unable to send sign up details to "+recipient, err);
			return false;
		}
	}
}
