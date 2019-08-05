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
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Prepares for global password setting.  Populates lists in globalPasswordSetterForm.
 *
 * @author  AO Industries, Inc.
 */
public class GlobalPasswordSetterAction extends PermissionAction {

	@Override
	public ActionForward executePermissionGranted(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		GlobalPasswordSetterForm globalPasswordSetterForm = (GlobalPasswordSetterForm)form;

		List<User> uns = aoConn.getAccount().getUser().getRows();

		List<String> packages = new ArrayList<>(uns.size());
		List<String> usernames = new ArrayList<>(uns.size());
		List<String> newPasswords = new ArrayList<>(uns.size());
		List<String> confirmPasswords = new ArrayList<>(uns.size());
		for(User un : uns) {
			if(un.canSetPassword()) {
				packages.add(un.getPackage().getName().toString());
				usernames.add(un.getUsername().toString());
				newPasswords.add("");
				confirmPasswords.add("");
			}
		}

		// Store to the form
		globalPasswordSetterForm.setPackages(packages);
		globalPasswordSetterForm.setUsernames(usernames);
		globalPasswordSetterForm.setNewPasswords(newPasswords);
		globalPasswordSetterForm.setConfirmPasswords(confirmPasswords);

		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		List<Permission.Name> permissions = new ArrayList<>();
		permissions.add(Permission.Name.set_business_administrator_password);
		permissions.add(Permission.Name.set_linux_server_account_password);
		permissions.add(Permission.Name.set_mysql_server_user_password);
		permissions.add(Permission.Name.set_postgres_server_user_password);
		return permissions;
	}
}
