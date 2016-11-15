/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2000-2009, 2016  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.ticket;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.Language;
import com.aoindustries.aoserv.client.TicketPriority;
import com.aoindustries.aoserv.client.TicketType;
import com.aoindustries.aoserv.client.validator.AccountingCode;
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
import org.apache.struts.action.ActionMessages;

/**
 * Provides the list of tickets.
 *
 * @author  AO Industries, Inc.
 */
public class CreateCompletedAction extends PermissionAction {

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
		TicketForm ticketForm = (TicketForm)form;

		// Validation
		ActionMessages errors = ticketForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		Business business = aoConn.getBusinesses().get(AccountingCode.valueOf(ticketForm.getAccounting()));
		if(business==null) throw new SQLException("Unable to find Business: "+ticketForm.getAccounting());
		Language language = aoConn.getLanguages().get(locale.getLanguage());
		if(language==null) {
			language = aoConn.getLanguages().get(Language.EN);
			if(language==null) throw new SQLException("Unable to find Language: "+Language.EN);
		}
		TicketType ticketType = aoConn.getTicketTypes().get(TicketType.SUPPORT);
		if(ticketType==null) throw new SQLException("Unable to find TicketType: "+TicketType.SUPPORT);
		TicketPriority clientPriority = aoConn.getTicketPriorities().get(ticketForm.getClientPriority());
		if(clientPriority==null) throw new SQLException("Unable to find TicketPriority: "+ticketForm.getClientPriority());
		int pkey = aoConn.getTickets().addTicket(
			siteSettings.getBrand(),
			business,
			language,
			null,
			ticketType,
			null,
			ticketForm.getSummary(),
			ticketForm.getDetails(),
			clientPriority,
			ticketForm.getContactEmails(),
			ticketForm.getContactPhoneNumbers()
		);
		request.setAttribute("pkey", pkey);

		return mapping.findForward("success");
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.add_ticket);
	}
}
