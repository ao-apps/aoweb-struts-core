/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.ticket;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.Ticket;
import com.aoindustries.aoserv.client.TicketPriority;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class EditCompletedAction extends PermissionAction {

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

		// Look for the existing ticket
		String pkeyS = request.getParameter("pkey");
		if(pkeyS==null) return mapping.findForward("index");
		int pkey;
		try {
			pkey = Integer.parseInt(pkeyS);
		} catch(NumberFormatException err) {
			return mapping.findForward("index");
		}
		Ticket ticket = aoConn.getTickets().get(pkey);
		if(ticket==null) {
			request.setAttribute(com.aoindustries.website.Constants.HTTP_SERVLET_RESPONSE_STATUS, HttpServletResponse.SC_NOT_FOUND);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
			return null;
		}
		request.setAttribute("ticket", ticket);

		// Validation
		ActionMessages errors = ticketForm.validate(mapping, request);
		if(errors!=null && !errors.isEmpty()) {
			saveErrors(request, errors);
			return mapping.findForward("input");
		}

		// Request attribute defaults
		boolean businessUpdated = false;
		boolean contactEmailsUpdated = false;
		boolean contactPhoneNumbersUpdated = false;
		boolean clientPriorityUpdated = false;
		boolean summaryUpdated = false;
		boolean annotationAdded = false;

		// Update anything that changed
		Business newBusiness = aoConn.getBusinesses().get(AccountingCode.valueOf(ticketForm.getAccounting()));
		if(newBusiness==null) throw new SQLException("Unable to find Business: "+ticketForm.getAccounting());
		Business oldBusiness = ticket.getBusiness();
		if(!newBusiness.equals(oldBusiness)) {
			ticket.setBusiness(oldBusiness, newBusiness);
			businessUpdated = true;
		}
		if(!ticketForm.getContactEmails().equals(ticket.getContactEmails())) {
			ticket.setContactEmails(ticketForm.getContactEmails());
			contactEmailsUpdated = true;
		}
		if(!ticketForm.getContactPhoneNumbers().equals(ticket.getContactPhoneNumbers())) {
			ticket.setContactPhoneNumbers(ticketForm.getContactPhoneNumbers());
			contactPhoneNumbersUpdated = true;
		}
		TicketPriority clientPriority = aoConn.getTicketPriorities().get(ticketForm.getClientPriority());
		if(clientPriority==null) throw new SQLException("Unable to find TicketPriority: "+ticketForm.getClientPriority());
		if(!clientPriority.equals(ticket.getClientPriority())) {
			ticket.setClientPriority(clientPriority);
			clientPriorityUpdated = true;
		}
		if(!ticketForm.getSummary().equals(ticket.getSummary())) {
			ticket.setSummary(ticketForm.getSummary());
			summaryUpdated = true;
		}
		if(ticketForm.getAnnotationSummary().length()>0) {
			ticket.addAnnotation(
				ticketForm.getAnnotationSummary(),
				ticketForm.getAnnotationDetails()
			);
			annotationAdded = true;
		}

		// Set the request attributes
		request.setAttribute("businessUpdated", businessUpdated);
		request.setAttribute("contactEmailsUpdated", contactEmailsUpdated);
		request.setAttribute("contactPhoneNumbersUpdated", contactPhoneNumbersUpdated);
		request.setAttribute("clientPriorityUpdated", clientPriorityUpdated);
		request.setAttribute("summaryUpdated", summaryUpdated);
		request.setAttribute("annotationAdded", annotationAdded);
		request.setAttribute(
			"nothingChanged",
			!businessUpdated
			&& !contactEmailsUpdated
			&& !contactPhoneNumbersUpdated
			&& !clientPriorityUpdated
			&& !summaryUpdated
			&& !annotationAdded
		);
		return mapping.findForward("success");
	}

	private static final List<AOServPermission.Permission> permissions = new ArrayList<AOServPermission.Permission>(2);
	private static final List<AOServPermission.Permission> unmodifiablePermissions = Collections.unmodifiableList(permissions);
	static {
		permissions.add(AOServPermission.Permission.add_ticket);
		permissions.add(AOServPermission.Permission.edit_ticket);
	}
	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return unmodifiablePermissions;
	}
}
