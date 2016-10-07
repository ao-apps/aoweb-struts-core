/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.password;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.AOServer;
import com.aoindustries.aoserv.client.PostgresServer;
import com.aoindustries.aoserv.client.PostgresServerUser;
import com.aoindustries.aoserv.client.Server;
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
				String username = usernames.get(c);
				String hostname = aoServers.get(c);
				Server server = aoConn.getServers().get(hostname);
				if(server==null) throw new SQLException("Unable to find Server: "+server);
				AOServer aoServer = server.getAOServer();
				if(aoServer==null) throw new SQLException("Unable to find AOServer: "+aoServer);
				String serverName = postgreSQLServers.get(c);
				PostgresServer ps = aoServer.getPostgresServer(serverName);
				if(ps==null) throw new SQLException("Unable to find PostgresServer: "+serverName+" on "+hostname);
				PostgresServerUser psu = ps.getPostgresServerUser(username);
				if(psu==null) throw new SQLException("Unable to find PostgresServerUser: "+username+" on "+serverName+" on "+hostname);
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
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.set_postgres_server_user_password);
	}
}
