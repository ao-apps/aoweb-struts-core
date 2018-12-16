/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.creditcards.AOServConnectorPrincipal;
import com.aoindustries.aoserv.creditcards.CreditCardFactory;
import com.aoindustries.aoserv.creditcards.CreditCardProcessorFactory;
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

/**
 * Deletes the credit card.
 *
 * @author  AO Industries, Inc.
 */
public class DeleteCreditCardCompletedAction extends PermissionAction {

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
		// Make sure the credit card still exists, redirect to credit-card-manager if doesn't
		CreditCard creditCard = null;
		String S = request.getParameter("pkey");
		if(S!=null && S.length()>0) {
			try {
				int id = Integer.parseInt(S);
				creditCard = aoConn.getPayment().getCreditCard().get(id);
			} catch(NumberFormatException err) {
				getServlet().log(null, err);
			}
		}
		if(creditCard==null) return mapping.findForward("credit-card-manager");

		String cardNumber = creditCard.getCardInfo();

		// Lookup the card in the root connector (to get access to the processor)
		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		CreditCard rootCreditCard = rootConn.getPayment().getCreditCard().get(creditCard.getPkey());
		if(rootCreditCard == null) throw new SQLException("Unable to find CreditCard: " + creditCard.getPkey());

		// Delete the card from the bank and persistence
		Processor rootAoservCCP = rootCreditCard.getCreditCardProcessor();
		com.aoindustries.creditcards.CreditCardProcessor processor = CreditCardProcessorFactory.getCreditCardProcessor(rootAoservCCP);
		processor.deleteCreditCard(
			new AOServConnectorPrincipal(rootConn, aoConn.getThisBusinessAdministrator().getUsername().getUsername().toString()),
			CreditCardFactory.getCreditCard(rootCreditCard)
		);

		// Set request attributes
		request.setAttribute("cardNumber", cardNumber);

		// Return status success
		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.delete_credit_card);
	}
}
