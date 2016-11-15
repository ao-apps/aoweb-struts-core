/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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
