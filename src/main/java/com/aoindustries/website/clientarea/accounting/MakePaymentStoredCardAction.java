/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Payment from stored credit card.
 *
 * @author  AO Industries, Inc.
 */
public class MakePaymentStoredCardAction extends PermissionAction {

	/**
	 * When permission denied, redirect straight to the new card step.
	 */
	@Override
	public ActionForward executePermissionDenied(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn,
		List<Permission> permissions
	) throws Exception {
		// Redirect when they don't have permissions to retrieve stored cards
		response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/accounting/make-payment-new-card.do?accounting="+request.getParameter("accounting")));
		return null;
	}

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
		MakePaymentStoredCardForm makePaymentStoredCardForm = (MakePaymentStoredCardForm)form;

		// Find the requested business
		String accounting = makePaymentStoredCardForm.getAccounting();
		Account account = accounting==null ? null : aoConn.getAccount().getAccount().get(Account.Name.valueOf(accounting));
		if(account == null) {
			// Redirect back to make-payment if business not found
			return mapping.findForward("make-payment");
		}

		// If the card pkey is "", new card was selected
		String pkeyString = makePaymentStoredCardForm.getPkey();
		if(pkeyString==null) {
			// pkey not provided, redirect back to make-payment
			return mapping.findForward("make-payment");
		}
		if("".equals(pkeyString)) {
			response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/accounting/make-payment-new-card.do?accounting="+request.getParameter("accounting")));
			return null;
		}

		int id;
		try {
			id = Integer.parseInt(pkeyString);
		} catch(NumberFormatException err) {
			// Can't parse int, redirect back to make-payment
			return mapping.findForward("make-payment");
		}
		CreditCard creditCard = aoConn.getPayment().getCreditCard().get(id);
		if(creditCard == null) {
			// creditCard not found, redirect back to make-payment
			return mapping.findForward("make-payment");
		}

		// Prompt for amount of payment defaults to current balance.
		BigDecimal balance = account.getAccountBalance();
		if(balance.signum()>0) {
			makePaymentStoredCardForm.setPaymentAmount(balance.toPlainString());
		} else {
			makePaymentStoredCardForm.setPaymentAmount("");
		}

		request.setAttribute("business", account);
		request.setAttribute("creditCard", creditCard);

		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.get_credit_cards);
	}
}
