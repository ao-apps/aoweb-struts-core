/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.CreditCard;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import com.aoindustries.website.signup.SignupBusinessActionHelper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
		int pkey;
		try {
			pkey = Integer.parseInt(persistenceId);
		} catch(NumberFormatException err) {
			getServlet().log(null, err);
			// Redirect back to credit-card-manager if persistenceId can't be parsed to int
			return mapping.findForward("credit-card-manager");
		}
		// Find the credit card
		CreditCard creditCard = aoConn.getCreditCards().get(pkey);
		if(creditCard==null) {
			// Redirect back to credit-card-manager if card no longer exists or is inaccessible
			return mapping.findForward("credit-card-manager");
		}

		// Populate the initial details from selected card
		editCreditCardForm.setIsActive(creditCard.getIsActive() ? "true" : "false");
		editCreditCardForm.setAccounting(creditCard.getBusiness().getAccounting().toString());
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
		List<String> expirationYears = new ArrayList<String>(12);
		int startYear = Calendar.getInstance().get(Calendar.YEAR);
		for(int c=0;c<12;c++) expirationYears.add(Integer.toString(startYear+c));

		// Build the list of countries
		List<SignupBusinessActionHelper.CountryOption> countryOptions = SignupBusinessActionHelper.getCountryOptions(SiteSettings.getInstance(context).getRootAOServConnector());

		// Store to request attributes
		request.setAttribute("expirationYears", expirationYears);
		request.setAttribute("countryOptions", countryOptions);
	}

	private static final List<AOServPermission.Permission> permissions;
	static {
		List<AOServPermission.Permission> newList = new ArrayList<AOServPermission.Permission>(2);
		newList.add(AOServPermission.Permission.get_credit_cards);
		newList.add(AOServPermission.Permission.edit_credit_card);
		permissions = Collections.unmodifiableList(newList);
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return permissions;
	}
}
