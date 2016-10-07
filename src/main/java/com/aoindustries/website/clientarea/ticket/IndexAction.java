/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.ticket;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Ticket;
import com.aoindustries.aoserv.client.TicketStatus;
import com.aoindustries.website.AuthenticatedAction;
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
 * Provides the list of tickets.
 *
 * @author  AO Industries, Inc.
 */
public class IndexAction  extends AuthenticatedAction {

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
		List<Ticket> tickets = aoConn.getTickets().getRows();

		List<Ticket> filteredTickets = new ArrayList<Ticket>(tickets.size());
		for(Ticket ticket : tickets) {
			// Only show support or project tickets here
			//String type = ticket.getTicketType().getType();
			//if(
			//    type.equals(TicketType.SUPPORT)
			//    || type.equals(TicketType.PROJECTS)
			//) {
				// Do not show junk or deleted tickets
				String status = ticket.getStatus().getStatus();
				if(
					!status.equals(TicketStatus.JUNK)
					&& !status.equals(TicketStatus.DELETED)
				) {
					filteredTickets.add(ticket);
				}
			//}
		}

		// Set request values
		request.setAttribute("tickets", filteredTickets);

		return mapping.findForward("success");
	}
}
