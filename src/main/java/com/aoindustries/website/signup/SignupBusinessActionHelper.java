/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CountryCode;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.website.SiteSettings;
import static com.aoindustries.website.signup.ApplicationResources.accessor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;

/**
 * Managed3Action and Dedicated3Action both use this to setup the request attributes.  This is implemented
 * here because inheritance is not possible and neither one is logically above the other.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupBusinessActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupBusinessActionHelper() {}

	public static void setRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request
	) throws IOException, SQLException {
		AOServConnector rootConn=SiteSettings.getInstance(servletContext).getRootAOServConnector();

		// Build the list of countries
		List<CountryOption> countryOptions = getCountryOptions(rootConn);

		// Store to the request
		request.setAttribute("countryOptions", countryOptions);
	}

	/**
	 * Gets the options for use in a country list.
	 * Note: you probably want to use the RootAOServConnector to provide a more helpful list than a
	 * default user connector.
	 *
	 * @see  RootAOServConnector
	 */
	public static List<CountryOption> getCountryOptions(AOServConnector aoConn) throws IOException, SQLException {
		// Build the list of countries
		List<CountryOption> countryOptions = new ArrayList<CountryOption>();
		countryOptions.add(new CountryOption("", "---"));
		final int prioritySize = 10;
		int[] priorityCounter = new int[1];
		boolean selectedOne = false;
	List<CountryCode> cc = aoConn.getCountryCodes().getCountryCodesByPriority(prioritySize, priorityCounter);
	for (int i = 0; i<cc.size(); i++) {
			if(priorityCounter[0]!=0 && i==priorityCounter[0]) {
				countryOptions.add(new CountryOption("", "---"));
			}
			String code = cc.get(i).getCode();
			String ccname = cc.get(i).getName();
			countryOptions.add(new CountryOption(code, ccname));
		}
		return countryOptions;
	}

	public static class CountryOption {

		final private String code;
		final private String name;

		private CountryOption(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}
	}

	public static String getBusinessCountry(AOServConnector rootConn, SignupBusinessForm signupBusinessForm) throws IOException, SQLException {
		return rootConn.getCountryCodes().get(signupBusinessForm.getBusinessCountry()).getName();
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		SignupBusinessForm signupBusinessForm
	) throws IOException, SQLException {
		// Lookup things needed by the view
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();

		// Store as request attribute for the view
		request.setAttribute("businessCountry", getBusinessCountry(rootConn, signupBusinessForm));
	}

	public static void printConfirmation(ChainWriter emailOut, AOServConnector rootConn, SignupBusinessForm signupBusinessForm) throws IOException, SQLException {
		emailOut.print("    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.required")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessName.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessName()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.required")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessPhone.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessPhone()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessFax.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessFax()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.required")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessAddress1.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessAddress1()).print("</td>\n"
					 + "    </tr>\n");
		if(!GenericValidator.isBlankOrNull(signupBusinessForm.getBusinessAddress2())) {
			emailOut.print("    <tr>\n"
						 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
						 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessAddress2.prompt")).print("</td>\n"
						 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessAddress2()).print("</td>\n"
						 + "    </tr>\n");
		}
		emailOut.print("    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.required")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessCity.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessCity()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessState.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessState()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.required")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessCountry.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(getBusinessCountry(rootConn, signupBusinessForm)).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupBusinessForm.businessZip.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(signupBusinessForm.getBusinessZip()).print("</td>\n"
					 + "    </tr>\n");
	}
}
