/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
