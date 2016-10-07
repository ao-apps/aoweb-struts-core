/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.CreditCard;
import com.aoindustries.aoserv.client.CreditCardTransaction;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Gets the list of businesses or redirects to next step if only one business accessible.
 *
 * @author  AO Industries, Inc.
 */
public class MakePaymentSelectCardAction extends PermissionAction {

	/**
	 * When permission denied, redirect straight to the new card step.
	 */
	@Override
	final public ActionForward executePermissionDenied(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn,
		List<AOServPermission> permissions
	) throws Exception {
		// Redirect when they don't have permissions to retrieve stored cards
		response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/accounting/make-payment-new-card.do?accounting="+request.getParameter("accounting")));
		return null;
	}

	@Override
	final public ActionForward executePermissionGranted(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		// Find the requested business
		String accounting = request.getParameter("accounting");
		Business business = accounting==null ? null : aoConn.getBusinesses().get(AccountingCode.valueOf(accounting));
		if(business==null) {
			// Redirect back to make-payment if business not found
			return mapping.findForward("make-payment");
		}

		// Get the list of active credit cards stored for this business
		List<CreditCard> allCreditCards = business.getCreditCards();
		List<CreditCard> creditCards = new ArrayList<CreditCard>(allCreditCards.size());
		for(CreditCard creditCard : allCreditCards) {
			if(creditCard.getDeactivatedOn()==null) creditCards.add(creditCard);
		}

		if(creditCards.isEmpty()) {
			// Redirect to new card if none stored
			response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/accounting/make-payment-new-card.do?accounting="+request.getParameter("accounting")));
			return null;
		} else {
			// Store to request attributes, return success
			request.setAttribute("business", business);
			request.setAttribute("creditCards", creditCards);
			CreditCardTransaction lastCCT = business.getLastCreditCardTransaction();
			request.setAttribute("lastPaymentCreditCard", lastCCT==null ? null : lastCCT.getCreditCardProviderUniqueId());
			return mapping.findForward("success");
		}
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.get_credit_cards);
	}
}
