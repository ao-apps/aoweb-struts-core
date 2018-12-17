/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.website.signup;

import com.aoindustries.creditcards.CreditCard;
import com.aoindustries.net.Email;
import com.aoindustries.validation.ValidationResult;
import com.aoindustries.website.SessionActionForm;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author  AO Industries, Inc.
 */
public class SignupBillingInformationForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private String billingContact;
	private String billingEmail;
	private boolean billingUseMonthly;
	private boolean billingPayOneYear;
	private String billingCardholderName;
	private String billingCardNumber;
	private String billingExpirationMonth;
	private String billingExpirationYear;
	private String billingStreetAddress;
	private String billingCity;
	private String billingState;
	private String billingZip;

	public SignupBillingInformationForm() {
		setBillingContact("");
		setBillingEmail("");
		setBillingUseMonthly(false);
		setBillingPayOneYear(false);
		setBillingCardholderName("");
		setBillingCardNumber("");
		setBillingExpirationMonth("");
		setBillingExpirationYear("");
		setBillingStreetAddress("");
		setBillingCity("");
		setBillingState("");
		setBillingZip("");
	}

	@Override
	public boolean isEmpty() {
		return
			"".equals(billingContact)
			&& "".equals(billingEmail)
			&& !billingUseMonthly
			&& !billingPayOneYear
			&& "".equals(billingCardholderName)
			&& "".equals(billingCardNumber)
			&& "".equals(billingExpirationMonth)
			&& "".equals(billingExpirationYear)
			&& "".equals(billingStreetAddress)
			&& "".equals(billingCity)
			&& "".equals(billingState)
			&& "".equals(billingZip)
		;
	}

	/*
	 * This is cleared in Dedicated5CompletedAction instead
	 *
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		billingUseMonthly = false;
		billingPayOneYear = false;
	}
	 */

	final public String getBillingContact() {
		return billingContact;
	}

	final public void setBillingContact(String billingContact) {
		this.billingContact = billingContact.trim();
	}

	final public String getBillingEmail() {
		return billingEmail;
	}

	final public void setBillingEmail(String billingEmail) {
		this.billingEmail = billingEmail.trim();
	}

	final public boolean getBillingUseMonthly() {
		return billingUseMonthly;
	}

	final public void setBillingUseMonthly(boolean billingUseMonthly) {
		this.billingUseMonthly = billingUseMonthly;
	}

	final public boolean getBillingPayOneYear() {
		return billingPayOneYear;
	}

	final public void setBillingPayOneYear(boolean billingPayOneYear) {
		this.billingPayOneYear = billingPayOneYear;
	}

	final public String getBillingCardholderName() {
		return billingCardholderName;
	}

	final public void setBillingCardholderName(String billingCardholderName) {
		this.billingCardholderName = billingCardholderName.trim();
	}

	final public String getBillingCardNumber() {
		return billingCardNumber;
	}

	final public void setBillingCardNumber(String billingCardNumber) {
		this.billingCardNumber = billingCardNumber.trim();
	}

	final public String getBillingExpirationMonth() {
		return billingExpirationMonth;
	}

	final public void setBillingExpirationMonth(String billingExpirationMonth) {
		this.billingExpirationMonth = billingExpirationMonth.trim();
	}

	final public String getBillingExpirationYear() {
		return billingExpirationYear;
	}

	final public void setBillingExpirationYear(String billingExpirationYear) {
		this.billingExpirationYear = billingExpirationYear.trim();
	}

	final public String getBillingStreetAddress() {
		return billingStreetAddress;
	}

	final public void setBillingStreetAddress(String billingStreetAddress) {
		this.billingStreetAddress = billingStreetAddress.trim();
	}

	final public String getBillingCity() {
		return billingCity;
	}

	final public void setBillingCity(String billingCity) {
		this.billingCity = billingCity.trim();
	}

	final public String getBillingState() {
		return billingState;
	}

	final public void setBillingState(String billingState) {
		this.billingState = billingState.trim();
	}

	final public String getBillingZip() {
		return billingZip;
	}

	final public void setBillingZip(String billingZip) {
		this.billingZip = billingZip.trim();
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		if(GenericValidator.isBlankOrNull(billingContact)) errors.add("billingContact", new ActionMessage("signupBillingInformationForm.billingContact.required"));
		if(GenericValidator.isBlankOrNull(billingEmail)) {
			errors.add("billingEmail", new ActionMessage("signupBillingInformationForm.billingEmail.required"));
		//} else if(!GenericValidator.isEmail(billingEmail)) {
		//	errors.add("billingEmail", new ActionMessage("signupBillingInformationForm.billingEmail.invalid"));
		} else {
			ValidationResult billingEmailCheck = Email.validate(billingEmail);
			if(!billingEmailCheck.isValid()) {
				errors.add("billingEmail", new ActionMessage(billingEmailCheck.toString(), false));
			}
		}
		if(GenericValidator.isBlankOrNull(billingCardholderName)) errors.add("billingCardholderName", new ActionMessage("signupBillingInformationForm.billingCardholderName.required"));
		if(GenericValidator.isBlankOrNull(billingCardNumber)) {
			errors.add("billingCardNumber", new ActionMessage("signupBillingInformationForm.billingCardNumber.required"));
		} else if(!GenericValidator.isCreditCard(CreditCard.numbersOnly(billingCardNumber))) {
			errors.add("billingCardNumber", new ActionMessage("signupBillingInformationForm.billingCardNumber.invalid"));
		}
		if(
			GenericValidator.isBlankOrNull(billingExpirationMonth)
			|| GenericValidator.isBlankOrNull(billingExpirationYear)
		) errors.add("billingExpirationDate", new ActionMessage("signupBillingInformationForm.billingExpirationDate.required"));
		if(GenericValidator.isBlankOrNull(billingStreetAddress)) errors.add("billingStreetAddress", new ActionMessage("signupBillingInformationForm.billingStreetAddress.required"));
		if(GenericValidator.isBlankOrNull(billingCity)) errors.add("billingCity", new ActionMessage("signupBillingInformationForm.billingCity.required"));
		if(GenericValidator.isBlankOrNull(billingState)) errors.add("billingState", new ActionMessage("signupBillingInformationForm.billingState.required"));
		if(GenericValidator.isBlankOrNull(billingZip)) errors.add("billingZip", new ActionMessage("signupBillingInformationForm.billingZip.required"));
		return errors;
	}

	/**
	 * @deprecated  Please call CreditCard.numbersOnly directly.
	 */
	@Deprecated
	public static String numbersOnly(String S) {
		return CreditCard.numbersOnly(S);
	}
}
