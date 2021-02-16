/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Profile;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.aoserv.creditcards.AOServConnectorPrincipal;
import com.aoindustries.aoserv.creditcards.CreditCardFactory;
import com.aoindustries.aoserv.creditcards.CreditCardProcessorFactory;
import com.aoindustries.creditcards.CreditCardProcessor;
import com.aoindustries.lang.Strings;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
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
public class EditCreditCardCompletedAction extends EditCreditCardAction {

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

		// Validation
		ActionMessages errors = editCreditCardForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			// Init request values before showing input
			initRequestAttributes(request, getServlet().getServletContext());
			request.setAttribute("creditCard", creditCard);
			return mapping.findForward("input");
		}

		// Tells the view layer what was updated
		boolean updatedCardDetails = false;
		boolean updatedCardNumber = false;
		boolean updatedExpirationDate = false;
		boolean reactivatedCard = false;

		// Root connector used to get processor
		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		CreditCard rootCreditCard = rootConn.getPayment().getCreditCard().get(creditCard.getPkey());
		if(rootCreditCard == null) throw new SQLException("Unable to find CreditCard: " + creditCard.getPkey());
		CreditCardProcessor rootProcessor = CreditCardProcessorFactory.getCreditCardProcessor(rootCreditCard.getCreditCardProcessor());
		// Get the values that are simply copied from account profile, instead of cluttering the credit card form with too many fields
		Profile profile = rootCreditCard.getAccount().getProfile();
		String profileEmail = MakePaymentNewCardCompletedAction.getFirstBillingEmail(profile);
		String profilePhone = profile == null ? null : Strings.trimNullIfEmpty(profile.getPhone());
		String profileFax = profile == null ? null : Strings.trimNullIfEmpty(profile.getFax());
		if(
			!nullOrBlankEquals(editCreditCardForm.getFirstName(), creditCard.getFirstName())
			|| !nullOrBlankEquals(editCreditCardForm.getLastName(), creditCard.getLastName())
			|| !nullOrBlankEquals(editCreditCardForm.getCompanyName(), creditCard.getCompanyName())
			|| !nullOrBlankEquals(profileEmail, Objects.toString(creditCard.getEmail(), null))
			|| !nullOrBlankEquals(profilePhone, creditCard.getPhone())
			|| !nullOrBlankEquals(profileFax, creditCard.getFax())
			|| !nullOrBlankEquals(editCreditCardForm.getStreetAddress1(), creditCard.getStreetAddress1())
			|| !nullOrBlankEquals(editCreditCardForm.getStreetAddress2(), creditCard.getStreetAddress2())
			|| !nullOrBlankEquals(editCreditCardForm.getCity(), creditCard.getCity())
			|| !nullOrBlankEquals(editCreditCardForm.getState(), creditCard.getState())
			|| !nullOrBlankEquals(editCreditCardForm.getPostalCode(), creditCard.getPostalCode())
			|| !nullOrBlankEquals(editCreditCardForm.getCountryCode(), creditCard.getCountryCode().getCode())
			|| !nullOrBlankEquals(editCreditCardForm.getDescription(), creditCard.getDescription())
		) {
			// Update all fields except card number and expiration
			com.aoindustries.creditcards.CreditCard storedCreditCard = CreditCardFactory.getCreditCard(rootCreditCard);
			// Update fields
			storedCreditCard.setFirstName(editCreditCardForm.getFirstName());
			storedCreditCard.setLastName(editCreditCardForm.getLastName());
			storedCreditCard.setCompanyName(editCreditCardForm.getCompanyName());
			storedCreditCard.setEmail(profileEmail);
			storedCreditCard.setPhone(profilePhone);
			storedCreditCard.setFax(profileFax);
			storedCreditCard.setStreetAddress1(editCreditCardForm.getStreetAddress1());
			storedCreditCard.setStreetAddress2(editCreditCardForm.getStreetAddress2());
			storedCreditCard.setCity(editCreditCardForm.getCity());
			storedCreditCard.setState(editCreditCardForm.getState());
			storedCreditCard.setPostalCode(editCreditCardForm.getPostalCode());
			storedCreditCard.setCountryCode(editCreditCardForm.getCountryCode());
			storedCreditCard.setComments(editCreditCardForm.getDescription());
			// Update persistence
			rootProcessor.updateCreditCard(
				new AOServConnectorPrincipal(rootConn, aoConn.getCurrentAdministrator().getUsername().getUsername().toString()),
				storedCreditCard
			);
			updatedCardDetails = true;
		}

		String newCardNumber = editCreditCardForm.getCardNumber();
		String newExpirationMonth = editCreditCardForm.getExpirationMonth();
		String newExpirationYear = editCreditCardForm.getExpirationYear();
		String newCardCode = editCreditCardForm.getCardCode();
		if(!GenericValidator.isBlankOrNull(newCardNumber)) {
			// Update card number and expiration
			rootProcessor.updateCreditCardNumberAndExpiration(
				new AOServConnectorPrincipal(rootConn, aoConn.getCurrentAdministrator().getUsername().getUsername().toString()),
				CreditCardFactory.getCreditCard(rootCreditCard),
				newCardNumber,
				Byte.parseByte(newExpirationMonth),
				Short.parseShort(newExpirationYear),
				newCardCode
			);
			updatedCardNumber = true;
			updatedExpirationDate = true;
		} else {
			if(
				!GenericValidator.isBlankOrNull(newExpirationMonth)
				&& !GenericValidator.isBlankOrNull(newExpirationYear)
			) {
				// Update expiration only
				rootProcessor.updateCreditCardExpiration(
					new AOServConnectorPrincipal(rootConn, aoConn.getCurrentAdministrator().getUsername().getUsername().toString()),
					CreditCardFactory.getCreditCard(rootCreditCard),
					Byte.parseByte(newExpirationMonth),
					Short.parseShort(newExpirationYear)
				);
				updatedExpirationDate = true;
			}
		}

		if(!creditCard.getIsActive()) {
			// Reactivate if not active
			creditCard.reactivate();
			reactivatedCard = true;
		}

		// Set the cardNumber request attribute
		String cardNumber;
		if(!GenericValidator.isBlankOrNull(editCreditCardForm.getCardNumber())) cardNumber = com.aoindustries.creditcards.CreditCard.maskCreditCardNumber(editCreditCardForm.getCardNumber());
		else cardNumber = creditCard.getCardInfo();
		request.setAttribute("cardNumber", cardNumber);

		// Store which steps were done
		request.setAttribute("updatedCardDetails", Boolean.toString(updatedCardDetails));
		request.setAttribute("updatedCardNumber", Boolean.toString(updatedCardNumber));
		request.setAttribute("updatedExpirationDate", Boolean.toString(updatedExpirationDate));
		request.setAttribute("reactivatedCard", Boolean.toString(reactivatedCard));

		return mapping.findForward("success");
	}

	/**
	 * Considers two strings equal, where null and "" are considered equal.
	 */
	private static boolean nullOrBlankEquals(String S1, String S2) {
		if(S1==null) S1="";
		if(S2==null) S2="";
		return S1.equals(S2);
	}
}
