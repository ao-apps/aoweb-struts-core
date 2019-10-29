/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.creditcards.AOServConnectorPrincipal;
import com.aoindustries.aoserv.creditcards.AccountGroup;
import com.aoindustries.aoserv.creditcards.CreditCardProcessorFactory;
import com.aoindustries.creditcards.CreditCard;
import com.aoindustries.creditcards.CreditCardProcessor;
import com.aoindustries.util.StringUtility;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.sql.SQLException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * Stores the credit card.
 *
 * @author  AO Industries, Inc.
 */
public class AddCreditCardCompletedAction extends AddCreditCardAction {

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
		Account account = account_name == null ? null : aoConn.getAccount().getAccount().get(Account.Name.valueOf(account_name));
		if(account == null) {
			// Redirect back to credit-card-manager if account not found
			return mapping.findForward("credit-card-manager");
		}

		// Validation
		ActionMessages errors = addCreditCardForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			// Init request values before showing input
			initRequestAttributes(request, getServlet().getServletContext());
			return mapping.findForward("input");
		}

		// Get the credit card processor for the root connector of this website
		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		CreditCardProcessor creditCardProcessor = CreditCardProcessorFactory.getCreditCardProcessor(rootConn);
		if(creditCardProcessor==null) throw new SQLException("Unable to find enabled CreditCardProcessor for root connector");

		// Add card
		if(!creditCardProcessor.canStoreCreditCards()) throw new SQLException("CreditCardProcessor indicates it does not support storing credit cards.");

		String principalName = aoConn.getCurrentAdministrator().getUsername().getUsername().toString();
		String groupName = account_name;
		Profile profile = account.getProfile();
		creditCardProcessor.storeCreditCard(new AOServConnectorPrincipal(rootConn, principalName),
			new AccountGroup(aoConn.getAccount().getAccount().get(Account.Name.valueOf(account_name)), groupName),
			new CreditCard(
				null, // persistenceUniqueId
				principalName,
				groupName,
				null, // providerId
				null, // providerUniqueId
				addCreditCardForm.getCardNumber(),
				null, // maskedCardNumber
				Byte.parseByte(addCreditCardForm.getExpirationMonth()),
				Short.parseShort(addCreditCardForm.getExpirationYear()),
				addCreditCardForm.getCardCode(),
				addCreditCardForm.getFirstName(),
				addCreditCardForm.getLastName(),
				addCreditCardForm.getCompanyName(),
				MakePaymentNewCardCompletedAction.getFirstBillingEmail(profile),
				profile == null ? null : StringUtility.trimNullIfEmpty(profile.getPhone()),
				profile == null ? null : StringUtility.trimNullIfEmpty(profile.getFax()),
				null, // customerId: TODO: Set from account.Account once there is a constant identifier (not subject to set_business_accounting command/API)
				null, // customerTaxId
				addCreditCardForm.getStreetAddress1(),
				addCreditCardForm.getStreetAddress2(),
				addCreditCardForm.getCity(),
				addCreditCardForm.getState(),
				addCreditCardForm.getPostalCode(),
				addCreditCardForm.getCountryCode(),
				addCreditCardForm.getDescription()
			)
		);

		request.setAttribute("cardNumber", CreditCard.maskCreditCardNumber(addCreditCardForm.getCardNumber()));

		return mapping.findForward("success");
	}
}
