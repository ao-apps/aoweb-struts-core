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
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.User;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.validator.UserId;
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
public class LinuxAccountPasswordSetterCompletedAction extends PermissionAction {

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
		LinuxAccountPasswordSetterForm linuxAccountPasswordSetterForm = (LinuxAccountPasswordSetterForm)form;

		// Validation
		ActionMessages errors = linuxAccountPasswordSetterForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		// Reset passwords here and clear the passwords from the form
		ActionMessages messages = new ActionMessages();
		List<String> usernames = linuxAccountPasswordSetterForm.getUsernames();
		List<String> aoServers = linuxAccountPasswordSetterForm.getAoServers();
		List<String> newPasswords = linuxAccountPasswordSetterForm.getNewPasswords();
		List<String> confirmPasswords = linuxAccountPasswordSetterForm.getConfirmPasswords();
		for(int c=0;c<usernames.size();c++) {
			String newPassword = newPasswords.get(c);
			if(newPassword.length()>0) {
				UserId username = UserId.valueOf(usernames.get(c));
				User la = aoConn.getLinuxAccounts().get(username);
				if(la==null) throw new SQLException("Unable to find User: "+username);
				String hostname = aoServers.get(c);
				Host server = aoConn.getServers().get(hostname);
				if(server==null) throw new SQLException("Unable to find Host: "+server);
				Server aoServer = server.getAOServer();
				if(aoServer==null) throw new SQLException("Unable to find Server: "+aoServer);
				UserServer lsa = la.getLinuxServerAccount(aoServer);
				if(lsa==null) throw new SQLException("Unable to find UserServer: "+username+" on "+hostname);
				lsa.setPassword(newPassword);
				messages.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage("password.linuxAccountPasswordSetter.field.confirmPasswords.passwordReset"));
				newPasswords.set(c, "");
				confirmPasswords.set(c, "");
			}
		}
		saveMessages(request, messages);

		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.set_linux_server_account_password);
	}
}
