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
package com.aoindustries.web.struts.clientarea.control.password;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.postgresql.Server;
import com.aoindustries.aoserv.client.postgresql.User;
import com.aoindustries.aoserv.client.postgresql.UserServer;
import com.aoindustries.web.struts.PermissionAction;
import com.aoindustries.web.struts.SiteSettings;
import com.aoindustries.web.struts.Skin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Prepares for administrator password setting.  Populates lists in postgreSQLPasswordSetterForm.
 *
 * @author  AO Industries, Inc.
 */
public class PostgreSQLPasswordSetterAction extends PermissionAction {

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
		PostgreSQLPasswordSetterForm postgreSQLPasswordSetterForm = (PostgreSQLPasswordSetterForm)form;

		List<UserServer> psus = aoConn.getPostgresql().getUserServer().getRows();

		List<String> packages = new ArrayList<>(psus.size());
		List<String> usernames = new ArrayList<>(psus.size());
		List<String> postgreSQLServers = new ArrayList<>(psus.size());
		List<String> servers = new ArrayList<>(psus.size());
		List<String> newPasswords = new ArrayList<>(psus.size());
		List<String> confirmPasswords = new ArrayList<>(psus.size());
		for(UserServer psu : psus) {
			if(psu.canSetPassword()) {
				User pu = psu.getPostgresUser();
				com.aoindustries.aoserv.client.account.User un = pu.getUsername();
				Server ps = psu.getPostgresServer();
				packages.add(un.getPackage().getName().toString());
				usernames.add(un.getUsername().toString());
				postgreSQLServers.add(ps.getName().toString());
				servers.add(ps.getLinuxServer().getHostname().toString());
				newPasswords.add("");
				confirmPasswords.add("");
			}
		}

		// Store to the form
		postgreSQLPasswordSetterForm.setPackages(packages);
		postgreSQLPasswordSetterForm.setUsernames(usernames);
		postgreSQLPasswordSetterForm.setPostgreSQLServers(postgreSQLServers);
		postgreSQLPasswordSetterForm.setServers(servers);
		postgreSQLPasswordSetterForm.setNewPasswords(newPasswords);
		postgreSQLPasswordSetterForm.setConfirmPasswords(confirmPasswords);

		return mapping.findForward("success");
	}

	@Override
	public Set<Permission.Name> getPermissions() {
		return Collections.singleton(Permission.Name.set_postgres_server_user_password);
	}
}
