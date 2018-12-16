/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2000-2009, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.postgresql.Server;
import com.aoindustries.aoserv.client.postgresql.UserServer;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.PostgresUserId;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * @author  AO Industries, Inc.
 */
public class PostgreSQLPasswordSetterCompletedAction extends PermissionAction {

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

		// Validation
		ActionMessages errors = postgreSQLPasswordSetterForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		// Reset passwords here and clear the passwords from the form
		ActionMessages messages = new ActionMessages();
		List<String> usernames = postgreSQLPasswordSetterForm.getUsernames();
		List<String> aoServers = postgreSQLPasswordSetterForm.getAoServers();
		List<String> postgreSQLServers = postgreSQLPasswordSetterForm.getPostgreSQLServers();
		List<String> newPasswords = postgreSQLPasswordSetterForm.getNewPasswords();
		List<String> confirmPasswords = postgreSQLPasswordSetterForm.getConfirmPasswords();
		for(int c=0;c<usernames.size();c++) {
			String newPassword = newPasswords.get(c);
			if(newPassword.length()>0) {
				PostgresUserId username = PostgresUserId.valueOf(usernames.get(c));
				String hostname = aoServers.get(c);
				Host host = aoConn.getNet().getHost().get(hostname);
				if(host == null) throw new SQLException("Unable to find Host: " + host);
				com.aoindustries.aoserv.client.linux.Server linuxServer = host.getAOServer();
				if(linuxServer == null) throw new SQLException("Unable to find Server: " + host);
				PostgresServerName serverName = PostgresServerName.valueOf(postgreSQLServers.get(c));
				Server ps = linuxServer.getPostgresServer(serverName);
				if(ps==null) throw new SQLException("Unable to find Server: "+serverName+" on "+hostname);
				UserServer psu = ps.getPostgresServerUser(username);
				if(psu==null) throw new SQLException("Unable to find UserServer: "+username+" on "+serverName+" on "+hostname);
				psu.setPassword(newPassword);
				messages.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage("password.postgreSQLPasswordSetter.field.confirmPasswords.passwordReset"));
				newPasswords.set(c, "");
				confirmPasswords.set(c, "");
			}
		}
		saveMessages(request, messages);

		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.set_postgres_server_user_password);
	}
}
