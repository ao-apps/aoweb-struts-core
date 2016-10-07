/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.creditcards.AOServConnectorPrincipal;
import com.aoindustries.aoserv.creditcards.BusinessGroup;
import com.aoindustries.aoserv.creditcards.CreditCardProcessorFactory;
import com.aoindustries.creditcards.CreditCard;
import com.aoindustries.creditcards.CreditCardProcessor;
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

		String accounting = addCreditCardForm.getAccounting();
		if(GenericValidator.isBlankOrNull(accounting)) {
			// Redirect back to credit-card-manager it no accounting selected
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

		creditCardProcessor.storeCreditCard(
			new AOServConnectorPrincipal(rootConn, aoConn.getThisBusinessAdministrator().getUsername().getUsername()),
			new BusinessGroup(aoConn.getBusinesses().get(AccountingCode.valueOf(accounting)), accounting),
			new CreditCard(
				null, // persistenceUniqueId
				null, // principalName
				null, // groupName
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
				null,
				null,
				null,
				null,
				null,
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
