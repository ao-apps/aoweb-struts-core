/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016  AO Industries, Inc.
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

import com.aoindustries.creditcards.TransactionResult;
import com.aoindustries.sql.SQLUtility;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author  AO Industries, Inc.
 */
public class MakePaymentNewCardForm extends AddCreditCardForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String paymentAmount;

	/**
	 * Should be one of "", "store", "automatic"
	 */
	private String storeCard;

	public MakePaymentNewCardForm() {
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setPaymentAmount("");
		setStoreCard("");
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		paymentAmount = paymentAmount.trim();
		if(paymentAmount.startsWith("$")) paymentAmount=paymentAmount.substring(1);
		this.paymentAmount = paymentAmount;
	}

	public String getStoreCard() {
		return storeCard;
	}

	public void setStoreCard(String storeCard) {
		this.storeCard = storeCard;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		if(GenericValidator.isBlankOrNull(paymentAmount)) {
			errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.required"));
		} else {
			try {
				// Make sure can parse as int-of-pennies format (Once we no longer use int-of-pennies, this should be removed)
				// Long-term plan is to use BigDecimal exclusively for all monetary values. - DRA 2007-10-09
				int pennies = SQLUtility.getPennies(this.paymentAmount);
				// Make sure can parse as BigDecimal, and is correct value
				BigDecimal paymentAmount = new BigDecimal(this.paymentAmount);
				if(paymentAmount.compareTo(BigDecimal.ZERO)<=0) {
					errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.mustBeGeaterThanZero"));
				} else if(paymentAmount.scale()>2) {
					// Must not have more than 2 decimal places
					errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.invalid"));
				}
			} catch(NumberFormatException err) {
				errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.invalid"));
			}
		}
		return errors;
	}

	@Override
	public ActionErrors mapTransactionError(TransactionResult.ErrorCode errorCode) {
		String errorString = errorCode.toString();
		ActionErrors errors = new ActionErrors();
		switch(errorCode) {
			case INVALID_AMOUNT:
				errors.add("paymentAmount", new ActionMessage(errorString, false));
				return errors;
			default:
				return super.mapTransactionError(errorCode);
		}
	}
}
