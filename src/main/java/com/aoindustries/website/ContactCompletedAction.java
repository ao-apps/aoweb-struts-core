/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2000-2009, 2015, 2016  AO Industries, Inc.
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
package com.aoindustries.website;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Language;
import com.aoindustries.aoserv.client.TicketPriority;
import com.aoindustries.aoserv.client.TicketType;
import java.sql.SQLException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * @author  AO Industries, Inc.
 */
public class ContactCompletedAction extends SkinAction {

	@Override
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin
	) throws Exception {
		ContactForm contactForm = (ContactForm)form;

		// Validation
		ActionMessages errors = contactForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		Language language = rootConn.getLanguages().get(locale.getLanguage());
		if(language==null) {
			language = rootConn.getLanguages().get(Language.EN);
			if(language==null) throw new SQLException("Unable to find Language: "+Language.EN);
		}
		TicketType ticketType = rootConn.getTicketTypes().get(TicketType.CONTACT);
		if(ticketType==null) throw new SQLException("Unable to find TicketType: "+TicketType.CONTACT);
		TicketPriority clientPriority = rootConn.getTicketPriorities().get(TicketPriority.NORMAL);
		if(clientPriority==null) throw new SQLException("Unable to find TicketPriority: "+TicketPriority.NORMAL);
		rootConn.getTickets().addTicket(
			siteSettings.getBrand(),
			null,
			language,
			null,
			ticketType,
			contactForm.getFrom(),
			contactForm.getSubject(),
			contactForm.getMessage(),
			clientPriority,
			contactForm.getFrom(),
			""
		);

		return mapping.findForward("success");
	}
}
