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
public class EditCreditCardForm extends CreditCardForm implements Serializable {

	private static final long serialVersionUID = 2L;

	private String persistenceId;
	private String isActive;

	public EditCreditCardForm() {
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setPersistenceId("");
		setIsActive("");
	}

	public String getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(String persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		// persistenceId
		if(GenericValidator.isBlankOrNull(persistenceId)) errors.add("persistenceId", new ActionMessage("editCreditCardForm.persistenceId.required"));

		// cardNumber
		String cardNumber = getCardNumber();
		if(
			!GenericValidator.isBlankOrNull(cardNumber)
			&& !GenericValidator.isCreditCard(CreditCard.numbersOnly(cardNumber))
		) errors.add("cardNumber", new ActionMessage("editCreditCardForm.cardNumber.invalid"));

		// expirationMonth and expirationYear required when cardNumber provided
		String expirationMonth = getExpirationMonth();
		String expirationYear = getExpirationYear();
		if(!GenericValidator.isBlankOrNull(cardNumber)) {
			if(
				GenericValidator.isBlankOrNull(expirationMonth)
				|| GenericValidator.isBlankOrNull(expirationYear)
			) errors.add("expirationDate", new ActionMessage("editCreditCardForm.expirationDate.required"));
		} else {
			// If either month or year provided, both must be provided
			if(
				!GenericValidator.isBlankOrNull(expirationMonth)
				&& GenericValidator.isBlankOrNull(expirationYear)
			) {
				errors.add("expirationDate", new ActionMessage("editCreditCardForm.expirationDate.monthWithoutYear"));
			} else if(
				GenericValidator.isBlankOrNull(expirationMonth)
				&& !GenericValidator.isBlankOrNull(expirationYear)
			) {
				errors.add("expirationDate", new ActionMessage("editCreditCardForm.expirationDate.yearWithoutMonth"));
			}
		}

		// cardCode required when cardNumber provided
		String cardCode = getCardCode();
		if(!GenericValidator.isBlankOrNull(cardNumber)) {
			if(GenericValidator.isBlankOrNull(cardCode)) errors.add("cardCode", new ActionMessage("editCreditCardForm.cardCode.required"));
			else {
				try {
					CreditCard.validateCardCode(cardCode);
				} catch(LocalizedIllegalArgumentException e) {
					errors.add("cardCode", new ActionMessage(e.getLocalizedMessage(), false));
				}
			}
		} else {
			if(!GenericValidator.isBlankOrNull(cardCode)) errors.add("cardCode", new ActionMessage("editCreditCardForm.cardCode.notAllowed"));
		}
		return errors;
	}
}
