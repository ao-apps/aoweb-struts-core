/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017, 2019  AO Industries, Inc.
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
package com.aoindustries.web.struts.signup;

import com.aoindustries.web.struts.SessionActionForm;
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
public class SignupOrganizationForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private String organizationName;
	private String organizationPhone;
	private String organizationFax;
	private String organizationAddress1;
	private String organizationAddress2;
	private String organizationCity;
	private String organizationState;
	private String organizationCountry;
	private String organizationZip;

	public SignupOrganizationForm() {
		setOrganizationName("");
		setOrganizationPhone("");
		setOrganizationFax("");
		setOrganizationAddress1("");
		setOrganizationAddress2("");
		setOrganizationCity("");
		setOrganizationState("");
		setOrganizationCountry("");
		setOrganizationZip("");
	}

	@Override
	public boolean isEmpty() {
		return
			"".equals(organizationName)
			&& "".equals(organizationPhone)
			&& "".equals(organizationFax)
			&& "".equals(organizationAddress1)
			&& "".equals(organizationAddress2)
			&& "".equals(organizationCity)
			&& "".equals(organizationState)
			&& "".equals(organizationCountry)
			&& "".equals(organizationZip)
		;
	}

	final public String getOrganizationName() {
		return organizationName;
	}

	final public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName.trim();
	}

	final public String getOrganizationPhone() {
		return organizationPhone;
	}

	final public void setOrganizationPhone(String organizationPhone) {
		this.organizationPhone = organizationPhone.trim();
	}

	final public String getOrganizationFax() {
		return organizationFax;
	}

	final public void setOrganizationFax(String organizationFax) {
		this.organizationFax = organizationFax.trim();
	}

	final public String getOrganizationAddress1() {
		return organizationAddress1;
	}

	final public void setOrganizationAddress1(String organizationAddress1) {
		this.organizationAddress1 = organizationAddress1.trim();
	}

	final public String getOrganizationAddress2() {
		return organizationAddress2;
	}

	final public void setOrganizationAddress2(String organizationAddress2) {
		this.organizationAddress2 = organizationAddress2.trim();
	}

	final public String getOrganizationCity() {
		return organizationCity;
	}

	final public void setOrganizationCity(String organizationCity) {
		this.organizationCity = organizationCity.trim();
	}

	final public String getOrganizationState() {
		return organizationState;
	}

	final public void setOrganizationState(String organizationState) {
		this.organizationState = organizationState.trim();
	}

	final public String getOrganizationCountry() {
		return organizationCountry;
	}

	final public void setOrganizationCountry(String organizationCountry) {
		this.organizationCountry = organizationCountry.trim();
	}

	final public String getOrganizationZip() {
		return organizationZip;
	}

	final public void setOrganizationZip(String organizationZip) {
		this.organizationZip = organizationZip.trim();
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		if(GenericValidator.isBlankOrNull(organizationName)) errors.add("organizationName", new ActionMessage("signupOrganizationForm.organizationName.required"));
		if(GenericValidator.isBlankOrNull(organizationPhone)) errors.add("organizationPhone", new ActionMessage("signupOrganizationForm.organizationPhone.required"));
		if(GenericValidator.isBlankOrNull(organizationAddress1)) errors.add("organizationAddress1", new ActionMessage("signupOrganizationForm.organizationAddress1.required"));
		if(GenericValidator.isBlankOrNull(organizationCity)) errors.add("organizationCity", new ActionMessage("signupOrganizationForm.organizationCity.required"));
		if(GenericValidator.isBlankOrNull(organizationCountry)) errors.add("organizationCountry", new ActionMessage("signupOrganizationForm.organizationCountry.required"));
		return errors;
	}
}
