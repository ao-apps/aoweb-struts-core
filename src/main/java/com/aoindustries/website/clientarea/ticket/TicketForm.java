/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2000-2009, 2015, 2016, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.ticket;

import com.aoindustries.net.Email;
import com.aoindustries.lang.Strings;
import com.aoindustries.validation.ValidationResult;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

/**
 * @author  AO Industries, Inc.
 */
public class TicketForm extends ValidatorForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private static String makeLines(String commasOrLines) {
		StringBuilder result = new StringBuilder();
		for(String line : Strings.splitLines(commasOrLines)) {
			for(String word : Strings.split(line, ',')) {
				word = word.trim();
				if(word.length()>0) {
					if(result.length()>0) result.append('\n');
					result.append(word);
				}
			}
		}
		return result.toString();
	}

	private String account;
	private String clientPriority;
	private String contactEmails;
	private String contactPhoneNumbers;
	private String summary;
	private String details;
	private String annotationSummary;
	private String annotationDetails;

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setAccount("");
		setClientPriority("");
		setContactEmails("");
		setContactPhoneNumbers("");
		setSummary("");
		setDetails("");
		setAnnotationSummary("");
		setAnnotationDetails("");
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary.trim();
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details.trim();
	}

	public String getAnnotationSummary() {
		return annotationSummary;
	}

	public void setAnnotationSummary(String annotationSummary) {
		this.annotationSummary = annotationSummary.trim();
	}

	public String getAnnotationDetails() {
		return annotationDetails;
	}

	public void setAnnotationDetails(String annotationDetails) {
		this.annotationDetails = annotationDetails.trim();
	}

	/**
	 * @return the clientPriority
	 */
	public String getClientPriority() {
		return clientPriority;
	}

	/**
	 * @param clientPriority the clientPriority to set
	 */
	public void setClientPriority(String clientPriority) {
		this.clientPriority = clientPriority;
	}

	/**
	 * @return the contactEmails
	 */
	public String getContactEmails() {
		return contactEmails;
	}

	/**
	 * @param contactEmails the contactEmails to set
	 */
	public void setContactEmails(String contactEmails) {
		this.contactEmails = makeLines(contactEmails);
	}

	/**
	 * @return the contactPhoneNumbers
	 */
	public String getContactPhoneNumbers() {
		return contactPhoneNumbers;
	}

	/**
	 * @param contactPhoneNumbers the contactPhoneNumbers to set
	 */
	public void setContactPhoneNumbers(String contactPhoneNumbers) {
		this.contactPhoneNumbers = makeLines(contactPhoneNumbers);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();

		// contactEmails must be valid email addresses
		if(getContactEmails().length()>0) {
			for(String email : Strings.splitLines(getContactEmails())) {
				ValidationResult emailCheck = Email.validate(email);
				if(!emailCheck.isValid()) {
					errors.add("contactEmails", new ActionMessage(emailCheck.toString(), false));
					break;
				}
				//if(!GenericValidator.isEmail(email)) {
				//	errors.add("contactEmails", new ActionMessage("TicketForm.field.contactEmails.invalid"));
				//	break;
				//}
			}
		}

		// annotationSummary required with either summary or details provided
		if(getAnnotationDetails().length()>0 && getAnnotationSummary().length()==0) {
			errors.add("annotationSummary", new ActionMessage("TicketForm.field.annotationSummary.required"));
		}
		return errors;
	}
}
