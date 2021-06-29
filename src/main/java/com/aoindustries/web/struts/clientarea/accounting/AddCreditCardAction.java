/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts.clientarea.accounting;

import com.aoapps.payments.CreditCard;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.web.struts.PermissionAction;
import com.aoindustries.web.struts.SiteSettings;
import com.aoindustries.web.struts.Skin;
import com.aoindustries.web.struts.signup.SignupOrganizationActionHelper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Gets the form for adding a credit card.
 *
 * @author  AO Industries, Inc.
 */
public class AddCreditCardAction extends PermissionAction {

	/**
	 * Parses the first name in a locale-specific manner.
	 */
	public static String getFirstName(String name, Locale userLocale) {
		if(name==null) return null;
		name=name.trim();
		if(userLocale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
			// Last then first
			int pos = name.lastIndexOf(' ');
			if(pos==-1) return "";
			else return name.substring(pos+1).trim();
		} else {
			// First then last
			int pos = name.indexOf(' ');
			if(pos==-1) return "";
			else return name.substring(0, pos).trim();
		}
	}

	/**
	 * Parses the last name in a locale-specific manner.
	 */
	public static String getLastName(String name, Locale userLocale) {
		if(name==null) return null;
		name=name.trim();
		if(userLocale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
			// Last then first
			int pos = name.lastIndexOf(' ');
			if(pos==-1) return name;
			else return name.substring(0, pos).trim();
		} else {
			// First then last
			int pos = name.indexOf(' ');
			if(pos==-1) return name;
			else return name.substring(pos+1).trim();
		}
	}

	@Override
	public ActionForward executePermissionGranted(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		AddCreditCardForm addCreditCardForm=(AddCreditCardForm)form;

		String account_name = addCreditCardForm.getAccount();
		if(GenericValidator.isBlankOrNull(account_name)) {
			// Redirect back to credit-card-manager if no account selected
			return mapping.findForward("credit-card-manager");
		}

		// Populate the initial details from the selected account name or authenticated user
		Account account = aoConn.getAccount().getAccount().get(Account.Name.valueOf(account_name));
		if(account == null) throw new SQLException("Unable to find Account: " + account_name);
		Profile profile = account.getProfile();
		if(profile!=null) {
			addCreditCardForm.setFirstName(getFirstName(profile.getBillingContact(), locale));
			addCreditCardForm.setLastName(getLastName(profile.getBillingContact(), locale));
			addCreditCardForm.setCompanyName(profile.getName());
			addCreditCardForm.setStreetAddress1(profile.getAddress1());
			addCreditCardForm.setStreetAddress2(profile.getAddress2());
			addCreditCardForm.setCity(profile.getCity());
			addCreditCardForm.setState(profile.getState());
			addCreditCardForm.setPostalCode(profile.getZIP());
			addCreditCardForm.setCountryCode(profile.getCountry().getCode());
		} else {
			Administrator thisBA = aoConn.getCurrentAdministrator();
			addCreditCardForm.setFirstName(getFirstName(thisBA.getName(), locale));
			addCreditCardForm.setLastName(getLastName(thisBA.getName(), locale));
			addCreditCardForm.setStreetAddress1(thisBA.getAddress1());
			addCreditCardForm.setStreetAddress2(thisBA.getAddress2());
			addCreditCardForm.setCity(thisBA.getCity());
			addCreditCardForm.setState(thisBA.getState());
			addCreditCardForm.setPostalCode(thisBA.getZIP());
			addCreditCardForm.setCountryCode(thisBA.getCountry()==null ? "" : thisBA.getCountry().getCode());
		}

		initRequestAttributes(request, getServlet().getServletContext());

		return mapping.findForward("success");
	}

	protected void initRequestAttributes(HttpServletRequest request, ServletContext context) throws SQLException, IOException {
		// Build the list of years
		List<String> expirationYears = new ArrayList<>(1 + CreditCard.EXPIRATION_YEARS_FUTURE);
		int startYear = new GregorianCalendar().get(Calendar.YEAR);
		for(int c = 0; c <= CreditCard.EXPIRATION_YEARS_FUTURE; c++) {
			expirationYears.add(Integer.toString(startYear + c));
		}

		// Build the list of countries
		// We use the root connector to provide a better set of country values
		List<SignupOrganizationActionHelper.CountryOption> countryOptions = SignupOrganizationActionHelper.getCountryOptions(SiteSettings.getInstance(context).getRootAOServConnector());

		// Store to request attributes
		request.setAttribute("expirationYears", expirationYears);
		request.setAttribute("countryOptions", countryOptions);
	}

	@Override
	public Set<Permission.Name> getPermissions() {
		return Collections.singleton(Permission.Name.add_credit_card);
	}
}
