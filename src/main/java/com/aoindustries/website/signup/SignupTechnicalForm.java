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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.net.Email;
import com.aoindustries.util.WrappedException;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.validation.ValidationResult;
import com.aoindustries.website.SessionActionForm;
import com.aoindustries.website.SiteSettings;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionServlet;

/**
 * @author  AO Industries, Inc.
 */
public class SignupTechnicalForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private String baName;
	private String baTitle;
	private String baWorkPhone;
	private String baCellPhone;
	private String baHomePhone;
	private String baFax;
	private String baEmail;
	private String baAddress1;
	private String baAddress2;
	private String baCity;
	private String baState;
	private String baCountry;
	private String baZip;
	private String baUsername;
	private String baPassword;

	public SignupTechnicalForm() {
		setBaName("");
		setBaTitle("");
		setBaWorkPhone("");
		setBaCellPhone("");
		setBaHomePhone("");
		setBaFax("");
		setBaEmail("");
		setBaAddress1("");
		setBaAddress2("");
		setBaCity("");
		setBaState("");
		setBaCountry("");
		setBaZip("");
		setBaUsername("");
		setBaPassword("");
	}

	@Override
	public boolean isEmpty() {
		return
			"".equals(baName)
			&& "".equals(baTitle)
			&& "".equals(baWorkPhone)
			&& "".equals(baCellPhone)
			&& "".equals(baHomePhone)
			&& "".equals(baFax)
			&& "".equals(baEmail)
			&& "".equals(baAddress1)
			&& "".equals(baAddress2)
			&& "".equals(baCity)
			&& "".equals(baState)
			&& "".equals(baCountry)
			&& "".equals(baZip)
			&& "".equals(baUsername)
			&& "".equals(baPassword)
		;
	}

	final public String getBaName() {
		return baName;
	}

	final public void setBaName(String baName) {
		this.baName = baName.trim();
	}

	final public String getBaTitle() {
		return baTitle;
	}

	final public void setBaTitle(String baTitle) {
		this.baTitle = baTitle.trim();
	}

	final public String getBaWorkPhone() {
		return baWorkPhone;
	}

	final public void setBaWorkPhone(String baWorkPhone) {
		this.baWorkPhone = baWorkPhone.trim();
	}

	final public String getBaCellPhone() {
		return baCellPhone;
	}

	final public void setBaCellPhone(String baCellPhone) {
		this.baCellPhone = baCellPhone.trim();
	}

	final public String getBaHomePhone() {
		return baHomePhone;
	}

	final public void setBaHomePhone(String baHomePhone) {
		this.baHomePhone = baHomePhone.trim();
	}

	final public String getBaFax() {
		return baFax;
	}

	final public void setBaFax(String baFax) {
		this.baFax = baFax.trim();
	}

	final public String getBaEmail() {
		return baEmail;
	}

	final public void setBaEmail(String baEmail) {
		this.baEmail = baEmail.trim();
	}

	final public String getBaAddress1() {
		return baAddress1;
	}

	final public void setBaAddress1(String baAddress1) {
		this.baAddress1 = baAddress1.trim();
	}

	final public String getBaAddress2() {
		return baAddress2;
	}

	final public void setBaAddress2(String baAddress2) {
		this.baAddress2 = baAddress2.trim();
	}

	final public String getBaCity() {
		return baCity;
	}

	final public void setBaCity(String baCity) {
		this.baCity = baCity.trim();
	}

	final public String getBaState() {
		return baState;
	}

	final public void setBaState(String baState) {
		this.baState = baState.trim();
	}

	final public String getBaCountry() {
		return baCountry;
	}

	final public void setBaCountry(String baCountry) {
		this.baCountry = baCountry.trim();
	}

	final public String getBaZip() {
		return baZip;
	}

	final public void setBaZip(String baZip) {
		this.baZip = baZip.trim();
	}

	final public String getBaUsername() {
		return baUsername;
	}

	final public void setBaUsername(String baUsername) {
		this.baUsername = baUsername.trim();
	}

	final public String getBaPassword() {
		return baPassword;
	}

	final public void setBaPassword(String baPassword) {
		this.baPassword = baPassword.trim();
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		try {
			if(GenericValidator.isBlankOrNull(baName)) errors.add("baName", new ActionMessage("signupTechnicalForm.baName.required"));
			if(GenericValidator.isBlankOrNull(baWorkPhone)) errors.add("baWorkPhone", new ActionMessage("signupTechnicalForm.baWorkPhone.required"));
			if(GenericValidator.isBlankOrNull(baEmail)) {
				errors.add("baEmail", new ActionMessage("signupTechnicalForm.baEmail.required"));
			//} else if(!GenericValidator.isEmail(baEmail)) {
			//	errors.add("baEmail", new ActionMessage("signupTechnicalForm.baEmail.invalid"));
			} else {
				ValidationResult baEmailCheck = Email.validate(baEmail);
				if(!baEmailCheck.isValid()) {
					errors.add("baEmail", new ActionMessage(baEmailCheck.toString(), false));
				}
			}
			if(GenericValidator.isBlankOrNull(baUsername)) errors.add("baUsername", new ActionMessage("signupTechnicalForm.baUsername.required"));
			else {
				ActionServlet myServlet = getServlet();
				if(myServlet!=null) {
					AOServConnector rootConn = SiteSettings.getInstance(myServlet.getServletContext()).getRootAOServConnector();
					String lowerUsername = baUsername.toLowerCase();
					ValidationResult check = User.Name.validate(lowerUsername);
					if(!check.isValid()) {
						errors.add("baUsername", new ActionMessage(check.toString(), false));
					} else {
						User.Name userId;
						try {
							userId = User.Name.valueOf(lowerUsername);
						} catch(ValidationException e) {
							AssertionError ae = new AssertionError("Already validated");
							ae.initCause(e);
							throw ae;
						}
						if(!rootConn.getAccount().getUser().isUsernameAvailable(userId)) errors.add("baUsername", new ActionMessage("signupTechnicalForm.baUsername.unavailable"));
					}
				}
			}
			return errors;
		} catch(IOException err) {
			throw new WrappedException(err);
		} catch(SQLException err) {
			throw new WrappedException(err);
		}
	}
}
