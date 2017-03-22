/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017  AO Industries, Inc.
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

	final public String getBusinessName() {
		return businessName;
	}

	final public void setBusinessName(String businessName) {
		this.businessName = businessName.trim();
	}

	final public String getBusinessPhone() {
		return businessPhone;
	}

	final public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone.trim();
	}

	final public String getBusinessFax() {
		return businessFax;
	}

	final public void setBusinessFax(String businessFax) {
		this.businessFax = businessFax.trim();
	}

	final public String getBusinessAddress1() {
		return businessAddress1;
	}

	final public void setBusinessAddress1(String businessAddress1) {
		this.businessAddress1 = businessAddress1.trim();
	}

	final public String getBusinessAddress2() {
		return businessAddress2;
	}

	final public void setBusinessAddress2(String businessAddress2) {
		this.businessAddress2 = businessAddress2.trim();
	}

	final public String getBusinessCity() {
		return businessCity;
	}

	final public void setBusinessCity(String businessCity) {
		this.businessCity = businessCity.trim();
	}

	final public String getBusinessState() {
		return businessState;
	}

	final public void setBusinessState(String businessState) {
		this.businessState = businessState.trim();
	}

	final public String getBusinessCountry() {
		return businessCountry;
	}

	final public void setBusinessCountry(String businessCountry) {
		this.businessCountry = businessCountry.trim();
	}

	final public String getBusinessZip() {
		return businessZip;
	}

	final public void setBusinessZip(String businessZip) {
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
