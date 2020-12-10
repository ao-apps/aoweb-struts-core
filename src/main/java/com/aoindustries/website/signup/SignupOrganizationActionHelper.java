/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.payment.CountryCode;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.html.Html;
import com.aoindustries.website.SiteSettings;
import static com.aoindustries.website.signup.Resources.PACKAGE_RESOURCES;
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
final public class SignupOrganizationActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupOrganizationActionHelper() {}

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
	 * @see  SiteSettings#getRootAOServConnector()
	 */
	public static List<CountryOption> getCountryOptions(AOServConnector aoConn) throws IOException, SQLException {
		// Build the list of countries
		List<CountryOption> countryOptions = new ArrayList<>();
		countryOptions.add(new CountryOption("", "---"));
		final int prioritySize = 10;
		int[] priorityCounter = new int[1];
		boolean selectedOne = false;
		List<CountryCode> cc = aoConn.getPayment().getCountryCode().getCountryCodesByPriority(prioritySize, priorityCounter);
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

	public static String getOrganizationCountry(AOServConnector rootConn, SignupOrganizationForm signupOrganizationForm) throws IOException, SQLException {
		return rootConn.getPayment().getCountryCode().get(signupOrganizationForm.getOrganizationCountry()).getName();
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		SignupOrganizationForm signupOrganizationForm
	) throws IOException, SQLException {
		// Lookup things needed by the view
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();

		// Store as request attribute for the view
		request.setAttribute("organizationCountry", getOrganizationCountry(rootConn, signupOrganizationForm));
	}

	public static void printConfirmation(ChainWriter emailOut, Html html, AOServConnector rootConn, SignupOrganizationForm signupOrganizationForm) throws IOException, SQLException {
		emailOut.print("    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.required"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationName.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationName()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.required"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationPhone.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationPhone()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationFax.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationFax()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.required"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationAddress1.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationAddress1()).print("</td>\n"
					 + "    </tr>\n");
		if(!GenericValidator.isBlankOrNull(signupOrganizationForm.getOrganizationAddress2())) {
			emailOut.print("    <tr>\n"
						 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired"));
		emailOut.print("</td>\n"
						 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationAddress2.prompt"));
		emailOut.print("</td>\n"
						 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationAddress2()).print("</td>\n"
						 + "    </tr>\n");
		}
		emailOut.print("    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.required"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationCity.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationCity()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationState.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationState()).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.required"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationCountry.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(getOrganizationCountry(rootConn, signupOrganizationForm)).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(PACKAGE_RESOURCES.getMessage("signupOrganizationForm.organizationZip.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(signupOrganizationForm.getOrganizationZip()).print("</td>\n"
					 + "    </tr>\n");
	}
}
