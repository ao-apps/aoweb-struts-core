/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2018  AO Industries, Inc.
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
 * Displays the list of credit cards.
 *
 * @author  AO Industries, Inc.
 */
public class CreditCardManagerAction extends PermissionAction {

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
		Account thisBusiness = aoConn.getThisBusinessAdministrator().getUsername().getPackage().getBusiness();

		// Create a map from business to list of credit cards
		List<BusinessAndCreditCards> businessCreditCards = new ArrayList<BusinessAndCreditCards>();
		for(Account account : aoConn.getAccount().getAccount().getRows()) {
			List<CreditCard> ccs = account.getCreditCards();
			if(
				thisBusiness.equals(account)
				|| !ccs.isEmpty()
				|| (
					account.getCanceled()==null
					&& !account.billParent()
				) || account.getAccountBalance().signum()!=0
			) {
				boolean hasActiveCard = false;
				for(CreditCard cc : ccs) {
					if(cc.getIsActive()) {
						hasActiveCard = true;
						break;
					}
				}
				businessCreditCards.add(new BusinessAndCreditCards(account, ccs, hasActiveCard));
			}
		}
		boolean showAccounting = aoConn.getAccount().getAccount().getRows().size() > 1;

		request.setAttribute("businessCreditCards", businessCreditCards);
		request.setAttribute("showAccounting", showAccounting ? "true" : "false");

		return mapping.findForward("success");
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.get_credit_cards);
	}

	public static class BusinessAndCreditCards {

		final private Account business;
		final private List<CreditCard> creditCards;
		final private boolean hasActiveCard;

		private BusinessAndCreditCards(Account business, List<CreditCard> creditCards, boolean hasActiveCard) {
			this.business=business;
			this.creditCards=creditCards;
			this.hasActiveCard=hasActiveCard;
		}

		public Account getBusiness() {
			return business;
		}

		public List<CreditCard> getCreditCards() {
			return creditCards;
		}

		public boolean getHasActiveCard() {
			return hasActiveCard;
		}
	}
}
