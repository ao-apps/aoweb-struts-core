/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2013, 2015, 2016, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.creditcards.CreditCard;
import com.aoindustries.website.AuthenticatedAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import com.aoindustries.website.signup.SignupBusinessActionHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
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
public class MakePaymentNewCardAction extends AuthenticatedAction {

	@Override
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		MakePaymentNewCardForm makePaymentNewCardForm=(MakePaymentNewCardForm)form;

		String accounting = makePaymentNewCardForm.getAccounting();
		if(GenericValidator.isBlankOrNull(accounting)) {
			// Redirect back to credit-card-manager it no accounting selected
			return mapping.findForward("make-payment");
		}

		// Populate the initial details from the selected accounting code or authenticated user
		Account account = aoConn.getAccount().getAccount().get(Account.Name.valueOf(accounting));
		if(account == null) throw new SQLException("Unable to find Account: " + accounting);
		Profile profile = account.getBusinessProfile();
		if(profile!=null) {
			makePaymentNewCardForm.setFirstName(AddCreditCardAction.getFirstName(profile.getBillingContact(), locale));
			makePaymentNewCardForm.setLastName(AddCreditCardAction.getLastName(profile.getBillingContact(), locale));
			makePaymentNewCardForm.setCompanyName(profile.getName());
			makePaymentNewCardForm.setStreetAddress1(profile.getAddress1());
			makePaymentNewCardForm.setStreetAddress2(profile.getAddress2());
			makePaymentNewCardForm.setCity(profile.getCity());
			makePaymentNewCardForm.setState(profile.getState());
			makePaymentNewCardForm.setPostalCode(profile.getZIP());
			makePaymentNewCardForm.setCountryCode(profile.getCountry().getCode());
		} else {
			Administrator thisBA = aoConn.getThisBusinessAdministrator();
			makePaymentNewCardForm.setFirstName(AddCreditCardAction.getFirstName(thisBA.getName(), locale));
			makePaymentNewCardForm.setLastName(AddCreditCardAction.getLastName(thisBA.getName(), locale));
			makePaymentNewCardForm.setStreetAddress1(thisBA.getAddress1());
			makePaymentNewCardForm.setStreetAddress2(thisBA.getAddress2());
			makePaymentNewCardForm.setCity(thisBA.getCity());
			makePaymentNewCardForm.setState(thisBA.getState());
			makePaymentNewCardForm.setPostalCode(thisBA.getZIP());
			makePaymentNewCardForm.setCountryCode(thisBA.getCountry()==null ? "" : thisBA.getCountry().getCode());
		}

		initRequestAttributes(request, getServlet().getServletContext());

		// Prompt for amount of payment defaults to current balance.
		BigDecimal balance = account.getAccountBalance();
		if(balance.signum()>0) {
			makePaymentNewCardForm.setPaymentAmount(balance.toPlainString());
		} else {
			makePaymentNewCardForm.setPaymentAmount("");
		}

		request.setAttribute("business", account);

		return mapping.findForward("success");
	}

	protected void initRequestAttributes(HttpServletRequest request, ServletContext context) throws SQLException, IOException {
		// Build the list of years
		List<String> expirationYears = new ArrayList<>(1 + CreditCard.EXPIRATION_YEARS_FUTURE);
		int startYear = Calendar.getInstance().get(Calendar.YEAR);
		for(int c = 0; c <= CreditCard.EXPIRATION_YEARS_FUTURE; c++) expirationYears.add(Integer.toString(startYear + c));

		// Build the list of countries
		// We use the root connector to provide a better set of country values
		List<SignupBusinessActionHelper.CountryOption> countryOptions = SignupBusinessActionHelper.getCountryOptions(SiteSettings.getInstance(context).getRootAOServConnector());

		// Store to request attributes
		request.setAttribute("expirationYears", expirationYears);
		request.setAttribute("countryOptions", countryOptions);
	}
}
