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
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.master.AOServPermission;
import com.aoindustries.aoserv.client.mysql.MySQLServer;
import com.aoindustries.aoserv.client.mysql.MySQLServerUser;
import com.aoindustries.aoserv.client.net.Server;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
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
public class MySQLPasswordSetterCompletedAction extends PermissionAction {

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
		MySQLPasswordSetterForm mySQLPasswordSetterForm = (MySQLPasswordSetterForm)form;

		// Validation
		ActionMessages errors = mySQLPasswordSetterForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		// Reset passwords here and clear the passwords from the form
		ActionMessages messages = new ActionMessages();
		List<String> usernames = mySQLPasswordSetterForm.getUsernames();
		List<String> aoServers = mySQLPasswordSetterForm.getAoServers();
		List<String> mySQLServers = mySQLPasswordSetterForm.getMySQLServers();
		List<String> newPasswords = mySQLPasswordSetterForm.getNewPasswords();
		List<String> confirmPasswords = mySQLPasswordSetterForm.getConfirmPasswords();
		for(int c=0;c<usernames.size();c++) {
			String newPassword = newPasswords.get(c);
			if(newPassword.length()>0) {
				MySQLUserId username = MySQLUserId.valueOf(usernames.get(c));
				String hostname = aoServers.get(c);
				Server server = aoConn.getServers().get(hostname);
				if(server==null) throw new SQLException("Unable to find Server: "+server);
				AOServer aoServer = server.getAOServer();
				if(aoServer==null) throw new SQLException("Unable to find AOServer: "+aoServer);
				MySQLServerName serverName = MySQLServerName.valueOf(mySQLServers.get(c));
				MySQLServer ms = aoServer.getMySQLServer(serverName);
				if(ms==null) throw new SQLException("Unable to find MySQLServer: "+serverName+" on "+hostname);
				MySQLServerUser msu = ms.getMySQLServerUser(username);
				if(msu==null) throw new SQLException("Unable to find MySQLServerUser: "+username+" on "+serverName+" on "+hostname);
				msu.setPassword(newPassword);
				messages.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage("password.mySQLPasswordSetter.field.confirmPasswords.passwordReset"));
				newPasswords.set(c, "");
				confirmPasswords.set(c, "");
			}
		}
		saveMessages(request, messages);

		return mapping.findForward("success");
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.set_mysql_server_user_password);
	}
}
