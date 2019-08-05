/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.website.AuthenticatedAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Prepares for administrator password setting.  Populates lists in administratorPasswordSetterForm.
 *
 * @author  AO Industries, Inc.
 */
public class AdministratorPasswordSetterAction extends AuthenticatedAction {

	@Override
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		AdministratorPasswordSetterForm administratorPasswordSetterForm = (AdministratorPasswordSetterForm)form;

		Administrator thisBA = aoConn.getCurrentAdministrator();

		List<Administrator> bas = thisBA.hasPermission(Permission.Name.set_business_administrator_password) ? aoConn.getAccount().getAdministrator().getRows() : Collections.singletonList(thisBA);

		List<String> packages = new ArrayList<>(bas.size());
		List<String> usernames = new ArrayList<>(bas.size());
		List<String> newPasswords = new ArrayList<>(bas.size());
		List<String> confirmPasswords = new ArrayList<>(bas.size());
		for(Administrator ba : bas) {
			if(ba.canSetPassword()) {
				User un = ba.getUsername();
				packages.add(un.getPackage().getName().toString());
				usernames.add(un.getUsername().toString());
				newPasswords.add("");
				confirmPasswords.add("");
			}
		}

		// Store to the form
		administratorPasswordSetterForm.setPackages(packages);
		administratorPasswordSetterForm.setUsernames(usernames);
		administratorPasswordSetterForm.setNewPasswords(newPasswords);
		administratorPasswordSetterForm.setConfirmPasswords(confirmPasswords);

		return mapping.findForward("success");
	}
}
