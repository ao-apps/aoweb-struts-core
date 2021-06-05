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
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import com.aoindustries.website.signup.SignupOrganizationActionHelper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
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
 * Gets the form for editing a credit card.
 *
 * @author  AO Industries, Inc.
 */
public class EditCreditCardAction extends PermissionAction {

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
		EditCreditCardForm editCreditCardForm=(EditCreditCardForm)form;

		String persistenceId = editCreditCardForm.getPersistenceId();
		if(GenericValidator.isBlankOrNull(persistenceId)) {
			// Redirect back to credit-card-manager if no persistenceId selected
			return mapping.findForward("credit-card-manager");
		}
		int id;
		try {
			id = Integer.parseInt(persistenceId);
		} catch(NumberFormatException err) {
			getServlet().log(null, err);
			// Redirect back to credit-card-manager if persistenceId can't be parsed to int
			return mapping.findForward("credit-card-manager");
		}
		// Find the credit card
		CreditCard creditCard = aoConn.getPayment().getCreditCard().get(id);
		if(creditCard == null) {
			// Redirect back to credit-card-manager if card no longer exists or is inaccessible
			return mapping.findForward("credit-card-manager");
		}

		// Populate the initial details from selected card
		editCreditCardForm.setIsActive(Boolean.toString(creditCard.getIsActive()));
		editCreditCardForm.setAccount(creditCard.getAccount_name().toString());
		editCreditCardForm.setFirstName(creditCard.getFirstName());
		editCreditCardForm.setLastName(creditCard.getLastName());
		editCreditCardForm.setCompanyName(creditCard.getCompanyName());
		editCreditCardForm.setStreetAddress1(creditCard.getStreetAddress1());
		editCreditCardForm.setStreetAddress2(creditCard.getStreetAddress2());
		editCreditCardForm.setCity(creditCard.getCity());
		editCreditCardForm.setState(creditCard.getState());
		editCreditCardForm.setPostalCode(creditCard.getPostalCode());
		editCreditCardForm.setCountryCode(creditCard.getCountryCode().getCode());
		editCreditCardForm.setDescription(creditCard.getDescription());

		initRequestAttributes(request, getServlet().getServletContext());

		request.setAttribute("creditCard", creditCard);

		return mapping.findForward("success");
	}

	protected void initRequestAttributes(HttpServletRequest request, ServletContext context) throws SQLException, IOException {
		// Build the list of years
		List<String> expirationYears = new ArrayList<>(1 + com.aoapps.payments.CreditCard.EXPIRATION_YEARS_FUTURE);
		int startYear = new GregorianCalendar().get(Calendar.YEAR);
		for(int c = 0; c <= com.aoapps.payments.CreditCard.EXPIRATION_YEARS_FUTURE; c++) {
			expirationYears.add(Integer.toString(startYear + c));
		}

		// Build the list of countries
		List<SignupOrganizationActionHelper.CountryOption> countryOptions = SignupOrganizationActionHelper.getCountryOptions(SiteSettings.getInstance(context).getRootAOServConnector());

		// Store to request attributes
		request.setAttribute("expirationYears", expirationYears);
		request.setAttribute("countryOptions", countryOptions);
	}

	private static final Set<Permission.Name> permissions = Collections.unmodifiableSet(
		EnumSet.of(
			Permission.Name.get_credit_cards,
			Permission.Name.edit_credit_card
		)
	);

	@Override
	public Set<Permission.Name> getPermissions() {
		return permissions;
	}
}
