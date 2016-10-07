/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Username;
import com.aoindustries.util.WrappedException;
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

	public String getBaName() {
		return baName;
	}

	public void setBaName(String baName) {
		this.baName = baName.trim();
	}

	public String getBaTitle() {
		return baTitle;
	}

	public void setBaTitle(String baTitle) {
		this.baTitle = baTitle.trim();
	}

	public String getBaWorkPhone() {
		return baWorkPhone;
	}

	public void setBaWorkPhone(String baWorkPhone) {
		this.baWorkPhone = baWorkPhone.trim();
	}

	public String getBaCellPhone() {
		return baCellPhone;
	}

	public void setBaCellPhone(String baCellPhone) {
		this.baCellPhone = baCellPhone.trim();
	}

	public String getBaHomePhone() {
		return baHomePhone;
	}

	public void setBaHomePhone(String baHomePhone) {
		this.baHomePhone = baHomePhone.trim();
	}

	public String getBaFax() {
		return baFax;
	}

	public void setBaFax(String baFax) {
		this.baFax = baFax.trim();
	}

	public String getBaEmail() {
		return baEmail;
	}

	public void setBaEmail(String baEmail) {
		this.baEmail = baEmail.trim();
	}

	public String getBaAddress1() {
		return baAddress1;
	}

	public void setBaAddress1(String baAddress1) {
		this.baAddress1 = baAddress1.trim();
	}

	public String getBaAddress2() {
		return baAddress2;
	}

	public void setBaAddress2(String baAddress2) {
		this.baAddress2 = baAddress2.trim();
	}

	public String getBaCity() {
		return baCity;
	}

	public void setBaCity(String baCity) {
		this.baCity = baCity.trim();
	}

	public String getBaState() {
		return baState;
	}

	public void setBaState(String baState) {
		this.baState = baState.trim();
	}

	public String getBaCountry() {
		return baCountry;
	}

	public void setBaCountry(String baCountry) {
		this.baCountry = baCountry.trim();
	}

	public String getBaZip() {
		return baZip;
	}

	public void setBaZip(String baZip) {
		this.baZip = baZip.trim();
	}

	public String getBaUsername() {
		return baUsername;
	}

	public void setBaUsername(String baUsername) {
		this.baUsername = baUsername.trim();
	}

	public String getBaPassword() {
		return baPassword;
	}

	public void setBaPassword(String baPassword) {
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
			} else if(!GenericValidator.isEmail(baEmail)) {
				errors.add("baEmail", new ActionMessage("signupTechnicalForm.baEmail.invalid"));
			}
			if(GenericValidator.isBlankOrNull(baUsername)) errors.add("baUsername", new ActionMessage("signupTechnicalForm.baUsername.required"));
			else {
				ActionServlet myServlet = getServlet();
				if(myServlet!=null) {
					AOServConnector rootConn = SiteSettings.getInstance(myServlet.getServletContext()).getRootAOServConnector();
					String lowerUsername = baUsername.toLowerCase();
					String check = Username.checkUsername(lowerUsername);
					if(check!=null) errors.add("baUsername", new ActionMessage(check, false));
					else if(!rootConn.getUsernames().isUsernameAvailable(lowerUsername)) errors.add("baUsername", new ActionMessage("signupTechnicalForm.baUsername.unavailable"));
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
