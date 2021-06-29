/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.lang.exception.WrappedException;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.PackageCategory;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.web.struts.SessionActionForm;
import com.aoindustries.web.struts.SiteSettings;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionServlet;

/**
 * @author  AO Industries, Inc.
 */
abstract public class SignupSelectPackageForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private int packageDefinition;

	public SignupSelectPackageForm() {
		setPackageDefinition(-1);
	}

	@Override
	public boolean isEmpty() {
		return packageDefinition == -1;
	}

	public int getPackageDefinition() {
		return packageDefinition;
	}

	final public void setPackageDefinition(int packageDefinition) {
		this.packageDefinition = packageDefinition;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		try {
			// Must be one of the active package_definitions
			ActionServlet myServlet = getServlet();
			if(myServlet!=null) {
				AOServConnector rootConn = SiteSettings.getInstance(myServlet.getServletContext()).getRootAOServConnector();
				PackageCategory category = rootConn.getBilling().getPackageCategory().get(getPackageCategory());
				Account rootAccount = rootConn.getCurrentAdministrator().getUsername().getPackage().getAccount();

				PackageDefinition pd = rootConn.getBilling().getPackageDefinition().get(packageDefinition);
				if(pd==null || !pd.getPackageCategory().equals(category) || !pd.getAccount().equals(rootAccount)) {
					errors.add("packageDefinition", new ActionMessage("signupSelectPackageForm.packageDefinition.required"));
				}
			}
			return errors;
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	abstract protected String getPackageCategory();
}
