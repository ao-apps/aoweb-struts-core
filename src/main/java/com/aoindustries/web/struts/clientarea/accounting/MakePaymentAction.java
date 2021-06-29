/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts.clientarea.accounting;

import com.aoapps.collections.AoCollections;
import com.aoapps.lang.i18n.Money;
import com.aoapps.lang.i18n.Monies;
import com.aoapps.net.URIEncoder;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.TransactionTable;
import com.aoindustries.web.struts.AuthenticatedAction;
import com.aoindustries.web.struts.SiteSettings;
import com.aoindustries.web.struts.Skin;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Gets the list of accounts or redirects to next step if only one account accessible.
 *
 * @author  AO Industries, Inc.
 */
public class MakePaymentAction extends AuthenticatedAction {

	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		TransactionTable transactionTable = aoConn.getBilling().getTransaction();
		Account thisAccount = aoConn.getCurrentAdministrator().getUsername().getPackage().getAccount();

		// Get the list of accounts that are not canceled or have a non-zero balance, or are thisAccount
		List<Account> allAccounts = aoConn.getAccount().getAccount().getRows();
		Map<Account, Monies> accountsAndBalances = AoCollections.newLinkedHashMap(allAccounts.size());
		for(Account account : allAccounts) {
			Monies accountBalance = transactionTable.getAccountBalance(account);
			if(
				thisAccount.equals(account)
				|| (
					account.getCanceled() == null
					&& !account.billParent()
				) || !accountBalance.isZero()
			) {
				// Remove all zero balances
				// This is useful when an account changes currencies and have paid their bill in old currency
				accountBalance = accountBalance.removeZeros();
				assert !accountBalance.isEmpty();

				if(account.getCanceled() == null) {
					// Add all currencies, as zero, for all current monthly charges (including billParent sub accounts)
					// This will allow payment in advance when there is no balance due
					Monies monthlyRate = account.getBillingMonthlyRate();
					if(monthlyRate != null) {
						for(Currency currency : monthlyRate.getCurrencies()) {
							accountBalance = accountBalance.add(new Money(currency, 0, 0));
						}
					}
				}
				accountsAndBalances.put(account, accountBalance);
			}
		}
		if(accountsAndBalances.size() == 1) {
			Map.Entry<Account, Monies> entry = accountsAndBalances.entrySet().iterator().next();
			Monies accountBalance = entry.getValue();
			Set<Currency> currencies = accountBalance.getCurrencies();
			if(currencies.size() == 1) {
				// Redirect, only one option
				response.sendRedirect(
					response.encodeRedirectURL(
						URIEncoder.encodeURI(
							skin.getUrlBase(request)
							+ "clientarea/accounting/make-payment-select-card.do?account="
							+ URIEncoder.encodeURIComponent(entry.getKey().getName().toString())
							+ "&currency="
							+ URIEncoder.encodeURIComponent(currencies.iterator().next().getCurrencyCode())
						)
					)
				);
				return null;
			}
		}
		// Show selector screen
		request.setAttribute("accountsAndBalances", accountsAndBalances);
		return mapping.findForward("success");
	}
}
