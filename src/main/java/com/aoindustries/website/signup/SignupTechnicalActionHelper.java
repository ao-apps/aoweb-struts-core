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
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.password.PasswordGenerator;
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
 * Managed4Action and Dedicated4Action both use this to setup the request attributes.  This is implemented
 * here because inheritance is not possible and neither one is logically above the other.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupTechnicalActionHelper {

	private static final int NUM_PASSWORD_OPTIONS = 16;

	/**
	 * Make no instances.
	 */
	private SignupTechnicalActionHelper() {}

	public static void setRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		SignupTechnicalForm signupTechnicalForm
	) throws IOException, SQLException {
		AOServConnector rootConn=SiteSettings.getInstance(servletContext).getRootAOServConnector();

		// Build the list of countries
		List<SignupOrganizationActionHelper.CountryOption> countryOptions = SignupOrganizationActionHelper.getCountryOptions(rootConn);

		// Generate random passwords, keeping the selected password at index 0
		List<String> passwords = new ArrayList<>(NUM_PASSWORD_OPTIONS);
		if(!GenericValidator.isBlankOrNull(signupTechnicalForm.getBaPassword())) passwords.add(signupTechnicalForm.getBaPassword());
		while(passwords.size() < NUM_PASSWORD_OPTIONS) {
			passwords.add(PasswordGenerator.generatePassword());
		}

		// Store to the request
		request.setAttribute("countryOptions", countryOptions);
		request.setAttribute("passwords", passwords);
	}

	public static String getBaCountry(AOServConnector rootConn, SignupTechnicalForm signupTechnicalForm) throws IOException, SQLException {
		String baCountry = signupTechnicalForm.getBaCountry();
		return baCountry==null || baCountry.length()==0 ? "" : rootConn.getPayment().getCountryCode().get(baCountry).getName();
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		SignupTechnicalForm signupTechnicalForm
	) throws IOException, SQLException {
		// Lookup things needed by the view
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();

		// Store as request attribute for the view
		request.setAttribute("baCountry", getBaCountry(rootConn, signupTechnicalForm));
	}

	public static void writeEmailConfirmation(
		Html html,
		AOServConnector rootConn,
		SignupTechnicalForm signupTechnicalForm
	) throws IOException, SQLException {
		html.out.write("    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baName.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaName()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baTitle.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaTitle()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baWorkPhone.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaWorkPhone()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baCellPhone.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaCellPhone()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baHomePhone.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaHomePhone()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baFax.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaFax()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baEmail.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaEmail()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baAddress1.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaAddress1()).out.write("</td>\n"
		+ "    </tr>\n");
		if(!GenericValidator.isBlankOrNull(signupTechnicalForm.getBaAddress2())) {
			html.out.write("    <tr>\n"
			+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
			+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baAddress2.prompt")); html.out.write("</td>\n"
			+ "        <td>"); html.text(signupTechnicalForm.getBaAddress2()).out.write("</td>\n"
			+ "    </tr>\n");
		}
		html.out.write("    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baCity.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaCity()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baState.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaState()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baCountry.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(getBaCountry(rootConn, signupTechnicalForm)).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baZip.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaZip()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baUsername.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaUsername()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupTechnicalForm.baPassword.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupTechnicalForm.getBaPassword()).out.write("</td>\n"
		+ "    </tr>\n");
	}
}
