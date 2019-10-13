/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2003-2009, 2016, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.control.account;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Performs the actual cancellation.
 *
 * @author  AO Industries, Inc.
 */
public class CancelFeedbackCompletedAction  extends PermissionAction {

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
		CancelFeedbackForm cancelFeedbackForm = (CancelFeedbackForm)form;
		String account_name = cancelFeedbackForm.getAccount();
		String reason = cancelFeedbackForm.getReason();

		Account account;
		if(GenericValidator.isBlankOrNull(account_name)) {
			account = null;
		} else {
			account = aoConn.getAccount().getAccount().get(Account.Name.valueOf(account_name));
		}
		if(account==null || !account.canCancel()) {
			return mapping.findForward("invalid-account");
		}

		// Do the actual cancellation
		account.cancel(reason);

		// Set request values
		request.setAttribute("account", account);

		return mapping.findForward("success");
	}

	@Override
	public Set<Permission.Name> getPermissions() {
		return Collections.singleton(Permission.Name.cancel_business);
	}
}
