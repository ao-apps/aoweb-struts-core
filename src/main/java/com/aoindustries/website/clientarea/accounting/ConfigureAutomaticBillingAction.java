/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2018, 2019  AO Industries, Inc.
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
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Allows the selection of the card to use for automatic billing.
 *
 * @author  AO Industries, Inc.
 */
public class ConfigureAutomaticBillingAction extends PermissionAction {

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
		// Business must be selected and accessible
		String accounting = request.getParameter("accounting");
		if(GenericValidator.isBlankOrNull(accounting)) {
			return mapping.findForward("credit-card-manager");
		}
		Account account = aoConn.getAccount().getAccount().get(Account.Name.valueOf(accounting));
		if(account == null) {
			return mapping.findForward("credit-card-manager");
		}

		// Get the list of cards for the business, must have at least one card.
		List<CreditCard> creditCards = account.getCreditCards();
		// Build list of active cards
		List<CreditCard> activeCards = new ArrayList<>(creditCards.size());
		CreditCard automaticCard = null;
		for(CreditCard creditCard : creditCards) {
			if(creditCard.getIsActive()) {
				activeCards.add(creditCard);
				// The first automatic card is used
				if(automaticCard==null && creditCard.getUseMonthly()) automaticCard = creditCard;
			}
		}
		if(activeCards.isEmpty()) {
			return mapping.findForward("credit-card-manager");
		}

		// Store request attributes
		request.setAttribute("business", account);
		request.setAttribute("activeCards", activeCards);
		request.setAttribute("automaticCard", automaticCard);

		return mapping.findForward("success");
	}

	private static final List<Permission.Name> permissions;
	static {
		List<Permission.Name> newList = new ArrayList<>(2);
		newList.add(Permission.Name.get_credit_cards);
		newList.add(Permission.Name.edit_credit_card);
		permissions = Collections.unmodifiableList(newList);
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return permissions;
	}
}
