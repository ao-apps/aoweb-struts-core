/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author  AO Industries, Inc.
 */
public class MakePaymentStoredCardForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accounting;
	private String pkey;
	private String paymentAmount;

	public MakePaymentStoredCardForm() {
	}

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setAccounting("");
		setPkey("");
		setPaymentAmount("");
	}

	public String getAccounting() {
		return accounting;
	}

	public void setAccounting(String accounting) {
		this.accounting = accounting;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		paymentAmount = paymentAmount.trim();
		if(paymentAmount.startsWith("$")) paymentAmount=paymentAmount.substring(1);
		this.paymentAmount = paymentAmount;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if(GenericValidator.isBlankOrNull(paymentAmount)) {
			errors.add("paymentAmount", new ActionMessage("makePaymentStoredCardForm.paymentAmount.required"));
		} else {
			try {
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
}
