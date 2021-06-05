/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.control.password;

import com.aoapps.collections.AutoGrowArrayList;
import com.aoapps.encoding.Serialization;
import com.aoapps.encoding.servlet.SerializationEE;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.validation.ValidationException;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.postgresql.User;
import com.aoindustries.website.AuthenticatedAction;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author AO Industries, Inc.
 */
public class PostgreSQLPasswordSetterForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> packages;
	private List<String> usernames;
	private List<String> postgreSQLServers;
	private List<String> servers;
	private List<String> newPasswords;
	private List<String> confirmPasswords;

	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		setPackages(new AutoGrowArrayList<>());
		setUsernames(new AutoGrowArrayList<>());
		setPostgreSQLServers(new AutoGrowArrayList<>());
		setServers(new AutoGrowArrayList<>());
		setNewPasswords(new AutoGrowArrayList<>());
		setConfirmPasswords(new AutoGrowArrayList<>());
	}

	final public List<String> getPackages() {
		return packages;
	}

	final public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	final public List<String> getUsernames() {
		return usernames;
	}

	final public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

	final public List<String> getPostgreSQLServers() {
		return postgreSQLServers;
	}

	final public void setPostgreSQLServers(List<String> postgreSQLServers) {
		this.postgreSQLServers = postgreSQLServers;
	}

	final public List<String> getServers() {
		return servers;
	}

	final public void setServers(List<String> servers) {
		this.servers = servers;
	}

	final public List<String> getNewPasswords() {
		return newPasswords;
	}

	final public void setNewPasswords(List<String> newPasswords) {
		this.newPasswords = newPasswords;
	}

	final public List<String> getConfirmPasswords() {
		return confirmPasswords;
	}

	final public void setConfirmPasswords(List<String> confirmPasswords) {
		this.confirmPasswords = confirmPasswords;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		try {
			ActionErrors errors = super.validate(mapping, request);
			if(errors==null) errors = new ActionErrors();
			AOServConnector aoConn = AuthenticatedAction.getAoConn(request, null);
			if(aoConn==null) throw new RuntimeException("aoConn is null");

			ServletContext servletContext = getServlet().getServletContext();

			for(int c=0;c<usernames.size();c++) {
				String newPassword = newPasswords.get(c);
				String confirmPassword = confirmPasswords.get(c);
				if(!newPassword.equals(confirmPassword)) {
					errors.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage("password.postgreSQLPasswordSetter.field.confirmPasswords.mismatch"));
				} else {
					if(newPassword.length()>0) {
						User.Name username = User.Name.valueOf(usernames.get(c));

						// Check the password strength
						List<PasswordChecker.Result> results = User.checkPassword(username, newPassword);
						if(PasswordChecker.hasResults(results)) {
							Serialization serialization = SerializationEE.get(servletContext, request);
							errors.add(
								"confirmPasswords[" + c + "].confirmPasswords",
								new ActionMessage(
									PasswordChecker.getResultsHtml(results, serialization == Serialization.XML),
									false
								)
							);
						}
					}
				}
			}
			return errors;
		} catch(IOException | ValidationException err) {
			throw new WrappedException(err);
		}
	}
}
