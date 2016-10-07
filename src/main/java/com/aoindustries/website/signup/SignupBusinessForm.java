/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

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
public class SignupBusinessForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private String businessName;
	private String businessPhone;
	private String businessFax;
	private String businessAddress1;
	private String businessAddress2;
	private String businessCity;
	private String businessState;
	private String businessCountry;
	private String businessZip;

	public SignupBusinessForm() {
		setBusinessName("");
		setBusinessPhone("");
		setBusinessFax("");
		setBusinessAddress1("");
		setBusinessAddress2("");
		setBusinessCity("");
		setBusinessState("");
		setBusinessCountry("");
		setBusinessZip("");
	}

	@Override
	public boolean isEmpty() {
		return
			"".equals(businessName)
			&& "".equals(businessPhone)
			&& "".equals(businessFax)
			&& "".equals(businessAddress1)
			&& "".equals(businessAddress2)
			&& "".equals(businessCity)
			&& "".equals(businessState)
			&& "".equals(businessCountry)
			&& "".equals(businessZip)
		;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName.trim();
	}

	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone.trim();
	}

	public String getBusinessFax() {
		return businessFax;
	}

	public void setBusinessFax(String businessFax) {
		this.businessFax = businessFax.trim();
	}

	public String getBusinessAddress1() {
		return businessAddress1;
	}

	public void setBusinessAddress1(String businessAddress1) {
		this.businessAddress1 = businessAddress1.trim();
	}

	public String getBusinessAddress2() {
		return businessAddress2;
	}

	public void setBusinessAddress2(String businessAddress2) {
		this.businessAddress2 = businessAddress2.trim();
	}

	public String getBusinessCity() {
		return businessCity;
	}

	public void setBusinessCity(String businessCity) {
		this.businessCity = businessCity.trim();
	}

	public String getBusinessState() {
		return businessState;
	}

	public void setBusinessState(String businessState) {
		this.businessState = businessState.trim();
	}

	public String getBusinessCountry() {
		return businessCountry;
	}

	public void setBusinessCountry(String businessCountry) {
		this.businessCountry = businessCountry.trim();
	}

	public String getBusinessZip() {
		return businessZip;
	}

	public void setBusinessZip(String businessZip) {
		this.businessZip = businessZip.trim();
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		if(GenericValidator.isBlankOrNull(businessName)) errors.add("businessName", new ActionMessage("signupBusinessForm.businessName.required"));
		if(GenericValidator.isBlankOrNull(businessPhone)) errors.add("businessPhone", new ActionMessage("signupBusinessForm.businessPhone.required"));
		if(GenericValidator.isBlankOrNull(businessAddress1)) errors.add("businessAddress1", new ActionMessage("signupBusinessForm.businessAddress1.required"));
		if(GenericValidator.isBlankOrNull(businessCity)) errors.add("businessCity", new ActionMessage("signupBusinessForm.businessCity.required"));
		if(GenericValidator.isBlankOrNull(businessCountry)) errors.add("businessCountry", new ActionMessage("signupBusinessForm.businessCountry.required"));
		return errors;
	}
}
