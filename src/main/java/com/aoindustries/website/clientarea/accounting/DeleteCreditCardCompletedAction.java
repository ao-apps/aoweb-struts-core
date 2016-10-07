/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.CreditCard;
import com.aoindustries.aoserv.client.CreditCardProcessor;
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
				int pkey = Integer.parseInt(S);
				creditCard = aoConn.getCreditCards().get(pkey);
			} catch(NumberFormatException err) {
				getServlet().log(null, err);
			}
		}
		if(creditCard==null) return mapping.findForward("credit-card-manager");

		String cardNumber = creditCard.getCardInfo();

		// Lookup the card in the root connector (to get access to the processor)
		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		CreditCard rootCreditCard = rootConn.getCreditCards().get(creditCard.getPkey());
		if(rootCreditCard==null) throw new SQLException("Unable to find CreditCard: "+creditCard.getPkey());

		// Delete the card from the bank and persistence
		CreditCardProcessor rootAoservCCP = rootCreditCard.getCreditCardProcessor();
		com.aoindustries.creditcards.CreditCardProcessor processor = CreditCardProcessorFactory.getCreditCardProcessor(rootAoservCCP);
		processor.deleteCreditCard(
			new AOServConnectorPrincipal(rootConn, aoConn.getThisBusinessAdministrator().getUsername().getUsername()),
			CreditCardFactory.getCreditCard(rootCreditCard)
		);

		// Set request attributes
		request.setAttribute("cardNumber", cardNumber);

		// Return status success
		return mapping.findForward("success");
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.delete_credit_card);
	}
}
