/*
 * Copyright 2009-2013, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Brand;
import com.aoindustries.aoserv.client.PackageDefinition;
import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.util.i18n.ThreadLocale;
import com.aoindustries.website.Mailer;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
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

	public static void sendSupportSummaryEmail(
		ActionServlet servlet,
		HttpServletRequest request,
		String pkey,
		String statusKey,
		SiteSettings siteSettings,
		PackageDefinition packageDefinition,
		SignupBusinessForm signupBusinessForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) {
		try {
			sendSummaryEmail(servlet, request, pkey, statusKey, siteSettings.getBrand().getAowebStrutsSignupAdminAddress(), siteSettings, packageDefinition, signupBusinessForm, signupTechnicalForm, signupBillingInformationForm);
		} catch(Exception err) {
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
		SignupBusinessForm signupBusinessForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) {
		Set<String> addresses = new HashSet<String>();
		addresses.add(signupTechnicalForm.getBaEmail());
		addresses.add(signupBillingInformationForm.getBillingEmail());
		Set<String> successAddresses = new HashSet<String>();
		Set<String> failureAddresses = new HashSet<String>();
		Iterator<String> I=addresses.iterator();
		while(I.hasNext()) {
			String address=I.next();
			boolean success = sendSummaryEmail(servlet, request, pkey, statusKey, address, siteSettings, packageDefinition, signupBusinessForm, signupTechnicalForm, signupBillingInformationForm);
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
	private static boolean sendSummaryEmail(
		ActionServlet servlet,
		HttpServletRequest request,
		String pkey,
		String statusKey,
		String recipient,
		SiteSettings siteSettings,
		PackageDefinition packageDefinition,
		SignupBusinessForm signupBusinessForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) {
		try {
			// Find the locale and related resource bundles
			Locale userLocale = ThreadLocale.get();
			String charset = Skin.getCharacterSet(userLocale);

			// Generate the email contents
			CharArrayWriter cout = new CharArrayWriter();
			ChainWriter emailOut = new ChainWriter(cout);
			String htmlLang = ServerConfirmationCompletedActionHelper.getHtmlLang(userLocale);
			emailOut.print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"");
			if(htmlLang!=null) emailOut.print(" lang=\"").print(htmlLang).print("\" xml:lang=\"").print(htmlLang).print('"');
			emailOut.print(">\n"
						 + "<head>\n"
						 + "    <meta http-equiv='Content-Type' content='text/html; charset=").print(charset).print("' />\n");
			// Embed the text-only style sheet
			InputStream cssIn = servlet.getServletContext().getResourceAsStream("/textskin/global.css");
			if(cssIn!=null) {
				try {
					emailOut.print("    <style type=\"text/css\">\n"
								 + "      /* <![CDATA[ */\n");
					Reader cssReader = new InputStreamReader(cssIn);
					try {
						char[] buff = new char[4096];
						int ret;
						while((ret=cssReader.read(buff, 0, 4096))!=-1) emailOut.write(buff, 0, ret);
					} finally {
						cssIn.close();
					}
					emailOut.print("      /* ]]> */\n"
								 + "    </style>\n");
				} finally {
					cssIn.close();
				}
			} else {
				servlet.log("Warning: Unable to find resource: /global/textskin.css");
			}
			emailOut.print("</head>\n"
						 + "<body>\n"
						 + "<table style='border:0px' cellpadding=\"0\" cellspacing=\"0\">\n"
						 + "    <tr><td style='white-space:nowrap' colspan=\"3\">\n"
						 + "        ").print(accessor.getMessage(statusKey, pkey)).print("<br />\n"
						 + "        <br />\n"
						 + "        ").print(accessor.getMessage("serverConfirmationCompleted.belowIsSummary")).print("<br />\n"
						 + "        <hr />\n"
						 + "    </td></tr>\n"
						 + "    <tr><th colspan=\"3\">").print(accessor.getMessage("steps.selectPackage.label")).print("</th></tr>\n");
			SignupSelectPackageActionHelper.printConfirmation(emailOut, packageDefinition);
			AOServConnector rootConn = siteSettings.getRootAOServConnector();
			emailOut.print("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
						 + "    <tr><th colspan=\"3\">").print(accessor.getMessage("steps.businessInfo.label")).print("</th></tr>\n");
			SignupBusinessActionHelper.printConfirmation(emailOut, rootConn, signupBusinessForm);
			emailOut.print("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
						 + "    <tr><th colspan=\"3\">").print(accessor.getMessage("steps.technicalInfo.label")).print("</th></tr>\n");
			SignupTechnicalActionHelper.printConfirmation(emailOut, rootConn, signupTechnicalForm);
			emailOut.print("    <tr><td colspan=\"3\">&#160;</td></tr>\n"
						 + "    <tr><th colspan=\"3\">").print(accessor.getMessage("steps.billingInformation.label")).print("</th></tr>\n");
			SignupBillingInformationActionHelper.printConfirmation(emailOut, signupBillingInformationForm);
			emailOut.print("</table>\n"
						 + "</body>\n"
						 + "</html>\n");
			emailOut.flush();

			// Send the email
			Brand brand = siteSettings.getBrand();
			Mailer.sendEmail(
				HostAddress.valueOf(brand.getSignupEmailAddress().getDomain().getAOServer().getHostname()),
				"text/html",
				charset,
				brand.getSignupEmailAddress().toString(),
				brand.getSignupEmailDisplay(),
				Collections.singletonList(recipient),
				accessor.getMessage("serverConfirmationCompleted.email.subject", pkey),
				cout.toString()
			);

			return true;
		} catch(RuntimeException err) {
			servlet.log("Unable to send sign up details to "+recipient, err);
			return false;
		} catch(IOException err) {
			servlet.log("Unable to send sign up details to "+recipient, err);
			return false;
		} catch(SQLException err) {
			servlet.log("Unable to send sign up details to "+recipient, err);
			return false;
		} catch(MessagingException err) {
			servlet.log("Unable to send sign up details to "+recipient, err);
			return false;
		}
	}
}
