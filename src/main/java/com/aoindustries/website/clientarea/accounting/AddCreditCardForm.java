/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.creditcards.CreditCard;
import com.aoindustries.lang.LocalizedIllegalArgumentException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author  AO Industries, Inc.
 */
public class AddCreditCardForm extends CreditCardForm implements Serializable {

	private static final long serialVersionUID = 2L;

	public AddCreditCardForm() {
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		// cardNumber
		String cardNumber = getCardNumber();
		if(GenericValidator.isBlankOrNull(cardNumber)) errors.add("cardNumber", new ActionMessage("addCreditCardForm.cardNumber.required"));
		else if(!GenericValidator.isCreditCard(CreditCard.numbersOnly(cardNumber))) errors.add("cardNumber", new ActionMessage("addCreditCardForm.cardNumber.invalid"));
		// expirationMonth and expirationYear
		String expirationMonth = getExpirationMonth();
		String expirationYear = getExpirationYear();
		if(
			GenericValidator.isBlankOrNull(expirationMonth)
			|| GenericValidator.isBlankOrNull(expirationYear)
		) errors.add("expirationDate", new ActionMessage("addCreditCardForm.expirationDate.required"));
		// cardCode
		String cardCode = getCardCode();
		if(GenericValidator.isBlankOrNull(cardCode)) errors.add("cardCode", new ActionMessage("addCreditCardForm.cardCode.required"));
		else {
			try {
				CreditCard.validateCardCode(cardCode);
			} catch(LocalizedIllegalArgumentException e) {
				errors.add("cardCode", new ActionMessage(e.getLocalizedMessage(), false));
			}
		}
		return errors;
	}
}
