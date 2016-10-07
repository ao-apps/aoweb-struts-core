/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.password;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.Username;
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
 * Prepares for business administrator password setting.  Populates lists in globalPasswordSetterForm.
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

		List<Username> uns = aoConn.getUsernames().getRows();

		List<String> packages = new ArrayList<String>(uns.size());
		List<String> usernames = new ArrayList<String>(uns.size());
		List<String> newPasswords = new ArrayList<String>(uns.size());
		List<String> confirmPasswords = new ArrayList<String>(uns.size());
		for(Username un : uns) {
			if(un.canSetPassword()) {
				packages.add(un.getPackage().getName());
				usernames.add(un.getUsername());
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
	public List<AOServPermission.Permission> getPermissions() {
		List<AOServPermission.Permission> permissions = new ArrayList<AOServPermission.Permission>();
		permissions.add(AOServPermission.Permission.set_business_administrator_password);
		permissions.add(AOServPermission.Permission.set_linux_server_account_password);
		permissions.add(AOServPermission.Permission.set_mysql_server_user_password);
		permissions.add(AOServPermission.Permission.set_postgres_server_user_password);
		return permissions;
	}
}
