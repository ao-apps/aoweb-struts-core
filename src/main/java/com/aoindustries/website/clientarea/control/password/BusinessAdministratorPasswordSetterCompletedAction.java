/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.password;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.website.AuthenticatedAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.sql.SQLException;
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
public class BusinessAdministratorPasswordSetterCompletedAction extends AuthenticatedAction {

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
		BusinessAdministratorPasswordSetterForm businessAdministratorPasswordSetterForm = (BusinessAdministratorPasswordSetterForm)form;

		// Validation
		ActionMessages errors = businessAdministratorPasswordSetterForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		// Reset passwords here and clear the passwords from the form
		BusinessAdministrator thisBA = aoConn.getThisBusinessAdministrator();
		ActionMessages messages = new ActionMessages();
		List<String> usernames = businessAdministratorPasswordSetterForm.getUsernames();
		List<String> newPasswords = businessAdministratorPasswordSetterForm.getNewPasswords();
		List<String> confirmPasswords = businessAdministratorPasswordSetterForm.getConfirmPasswords();
		for(int c=0;c<usernames.size();c++) {
			String newPassword = newPasswords.get(c);
			if(newPassword.length()>0) {
				String username = usernames.get(c);
				if(!thisBA.hasPermission(AOServPermission.Permission.set_business_administrator_password) && !thisBA.getUsername().getUsername().equals(username)) {
					AOServPermission aoPerm = aoConn.getAoservPermissions().get(AOServPermission.Permission.set_business_administrator_password);
					if(aoPerm==null) throw new SQLException("Unable to find AOServPermission: "+AOServPermission.Permission.set_business_administrator_password);
					request.setAttribute("permission", aoPerm);
					ActionForward forward = mapping.findForward("permission-denied");
					if(forward==null) throw new Exception("Unable to find ActionForward: permission-denied");
					return forward;
				}
				BusinessAdministrator ba = aoConn.getBusinessAdministrators().get(username);
				if(ba==null) throw new SQLException("Unable to find BusinessAdministrator: "+username);
				ba.setPassword(newPassword);
				messages.add("confirmPasswords[" + c + "].confirmPasswords", new ActionMessage("password.businessAdministratorPasswordSetter.field.confirmPasswords.passwordReset"));
				newPasswords.set(c, "");
				confirmPasswords.set(c, "");
			}
		}
		saveMessages(request, messages);

		return mapping.findForward("success");
	}
}
