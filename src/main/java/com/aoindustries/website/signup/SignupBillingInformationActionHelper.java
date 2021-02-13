/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.creditcards.CreditCard;
import com.aoindustries.html.Html;
import static com.aoindustries.website.signup.Resources.PACKAGE_RESOURCES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Managed5Action and Dedicated5Action both use this to setup the request attributes.  This is implemented
 * here because inheritance is not possible and neither one is logically above the other.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupBillingInformationActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupBillingInformationActionHelper() {}

	public static void setRequestAttributes(HttpServletRequest request) {
		setBillingExpirationYearsRequestAttribute(request);
	}

	public static void setBillingExpirationYearsRequestAttribute(HttpServletRequest request) {
		// Build the list of years
		List<String> billingExpirationYears = new ArrayList<>(1 + CreditCard.EXPIRATION_YEARS_FUTURE);
		int startYear = new GregorianCalendar().get(Calendar.YEAR);
		for(int c = 0; c <= CreditCard.EXPIRATION_YEARS_FUTURE; c++) {
			billingExpirationYears.add(Integer.toString(startYear + c));
		}

		// Store to request attributes
		request.setAttribute("billingExpirationYears", billingExpirationYears);
	}

	/**
	 * Only shows the first two and last four digits of a card number.
	 *
	 * @deprecated  Please call CreditCard.maskCreditCardNumber directly.
	 */
	@Deprecated
	public static String hideCreditCardNumber(String number) {
		return CreditCard.maskCreditCardNumber(number);
	}

	public static String getBillingCardNumber(SignupBillingInformationForm signupBillingInformationForm) {
		return CreditCard.maskCreditCardNumber(signupBillingInformationForm.getBillingCardNumber());
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		SignupBillingInformationForm signupBillingInformationForm
	) throws IOException {
		// Store as request attribute for the view
		request.setAttribute("billingCardNumber", getBillingCardNumber(signupBillingInformationForm));
	}

	public static void writeEmailConfirmation(
		Html html,
		SignupBillingInformationForm signupBillingInformationForm
	) throws IOException {
		html.out.write("    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingContact.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingContact()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingEmail.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingEmail()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingCardholderName.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingCardholderName()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingCardNumber.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(getBillingCardNumber(signupBillingInformationForm)).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingExpirationDate.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingExpirationDate.hidden")); html.out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingStreetAddress.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingStreetAddress()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingCity.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingCity()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingState.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingState()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.required")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingZip.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(signupBillingInformationForm.getBillingZip()).out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingUseMonthly.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage(signupBillingInformationForm.getBillingUseMonthly() ? "signupBillingInformationForm.billingUseMonthly.yes" : "signupBillingInformationForm.billingUseMonthly.no")); html.out.write("</td>\n"
		+ "    </tr>\n"
		+ "    <tr>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signup.notRequired")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage("signupBillingInformationForm.billingPayOneYear.prompt")); html.out.write("</td>\n"
		+ "        <td>"); html.text(PACKAGE_RESOURCES.getMessage(signupBillingInformationForm.getBillingPayOneYear() ? "signupBillingInformationForm.billingPayOneYear.yes" : "signupBillingInformationForm.billingPayOneYear.no")); html.out.write("</td>\n"
		+ "    </tr>\n");
	}
}
