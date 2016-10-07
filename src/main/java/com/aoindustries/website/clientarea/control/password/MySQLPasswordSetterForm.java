/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.password;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.MySQLUser;
import com.aoindustries.aoserv.client.PasswordChecker;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.util.AutoGrowArrayList;
import com.aoindustries.util.WrappedException;
import com.aoindustries.website.AuthenticatedAction;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Dan Armstrong &lt;dan@aoindustries.com&gt;
 */
public class MySQLPasswordSetterForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> packages;
	private List<String> usernames;
	private List<String> mySQLServers;
	private List<String> aoServers;
	private List<String> newPasswords;
	private List<String> confirmPasswords;

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setPackages(new AutoGrowArrayList<String>());
		setUsernames(new AutoGrowArrayList<String>());
		setMySQLServers(new AutoGrowArrayList<String>());
		setAoServers(new AutoGrowArrayList<String>());
		setNewPasswords(new AutoGrowArrayList<String>());
		setConfirmPasswords(new AutoGrowArrayList<String>());
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public List<String> getUsernames() {
		return usernames;
	}

	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	public List<String> getMySQLServers() {
		return mySQLServers;
	}

	public void setMySQLServers(List<String> mySQLServers) {
		this.mySQLServers = mySQLServers;
	}

	public List<String> getAoServers() {
		return aoServers;
	}

	public void setAoServers(List<String> aoServers) {
		this.aoServers = aoServers;
	}

	public List<String> getNewPasswords() {
		return newPasswords;
	}

	public void setNewPasswords(List<String> newPasswords) {
		this.newPasswords = newPasswords;
	}

	public List<String> getConfirmPasswords() {
		return confirmPasswords;
	}

	public void setConfirmPasswords(List<String> confirmPasswords) {
		this.confirmPasswords = confirmPasswords;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		try {
			ActionErrors errors = super.validate(mapping, request);
			if(errors==null) errors = new ActionErrors();
			AOServConnector aoConn = AuthenticatedAction.getAoConn(request, null);
			if(aoConn==null) throw new RuntimeException("aoConn is null");

			for(int c=0;c<usernames.size();c++) {
				String newPassword = newPasswords.get(c);
				String confirmPassword = confirmPasswords.get(c);
				if(!newPassword.equals(confirmPassword)) {
					errors.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage("password.mySQLPasswordSetter.field.confirmPasswords.mismatch"));
				} else {
					if(newPassword.length()>0) {
						String username = usernames.get(c);

						// Check the password strength
						List<PasswordChecker.Result> results = MySQLUser.checkPassword(MySQLUserId.valueOf(username), newPassword);
						if(PasswordChecker.hasResults(results)) {
							errors.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage(PasswordChecker.getResultsHtml(results), false));
						}
					}
				}
			}
			return errors;
		} catch(IOException err) {
			throw new WrappedException(err);
		} catch(ValidationException err) {
			throw new WrappedException(err);
		}
	}
}
