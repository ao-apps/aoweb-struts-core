/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.billing.Currency;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.payment.CreditCard;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.validation.ValidationException;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.validator.GenericValidator;
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
		String encoding = response.getCharacterEncoding();
		String currency = request.getParameter("currency");
		StringBuilder href = new StringBuilder();
		href
			.append(skin.getUrlBase(request))
			.append("clientarea/accounting/make-payment-new-card.do?account=")
			.append(URLEncoder.encode(request.getParameter("account"), encoding));
		if(!GenericValidator.isBlankOrNull(currency)) {
			href
				.append("&currency=")
				.append(URLEncoder.encode(request.getParameter("currency"), encoding));
		}
		response.sendRedirect(response.encodeRedirectURL(href.toString()));
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

		Account account;
		try {
			account = aoConn.getAccount().getAccount().get(Account.Name.valueOf(makePaymentStoredCardForm.getAccount()));
		} catch(ValidationException e) {
			return mapping.findForward("make-payment");
		}
		if(account == null) {
			// Redirect back to make-payment if account not found
			return mapping.findForward("make-payment");
		}

		Currency currency = aoConn.getBilling().getCurrency().get(makePaymentStoredCardForm.getCurrency());

		// If the card id is "", new card was selected
		String idString = makePaymentStoredCardForm.getId();
		if(idString == null) {
			// id not provided, redirect back to make-payment
			return mapping.findForward("make-payment");
		}
		if(idString.isEmpty()) {
			String encoding = response.getCharacterEncoding();
			StringBuilder href = new StringBuilder();
			href
				.append(skin.getUrlBase(request))
				.append("clientarea/accounting/make-payment-new-card.do?account=")
				.append(URLEncoder.encode(request.getParameter("account"), encoding));
			if(currency != null) {
				href
					.append("&currency=")
					.append(URLEncoder.encode(currency.getCurrencyCode(), encoding));
			}
			response.sendRedirect(response.encodeRedirectURL(href.toString()));
			return null;
		}

		int id;
		try {
			id = Integer.parseInt(idString);
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
		if(currency != null) {
			Money balance = aoConn.getBilling().getTransaction().getAccountBalance(account).get(currency.getCurrency());
			if(balance != null && balance.getUnscaledValue() > 0) {
				makePaymentStoredCardForm.setPaymentAmount(balance.getValue().toPlainString());
			} else {
				makePaymentStoredCardForm.setPaymentAmount("");
			}
		} else {
			// No currency, no default payment amount
			makePaymentStoredCardForm.setPaymentAmount("");
		}

		request.setAttribute("account", account);
		request.setAttribute("creditCard", creditCard);

		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.get_credit_cards);
	}
}
