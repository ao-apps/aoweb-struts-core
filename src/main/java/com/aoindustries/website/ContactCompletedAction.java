/*
 * Copyright 2000-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
