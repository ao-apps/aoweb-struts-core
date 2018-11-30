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
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Configures the selection of the card to use for automatic billing.
 *
 * @author  AO Industries, Inc.
 */
public class ConfigureAutomaticBillingCompletedAction extends PermissionAction {

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
		Account business = aoConn.getBusinesses().get(AccountingCode.valueOf(accounting));
		if(business==null) {
			return mapping.findForward("credit-card-manager");
		}

		// CreditCard must be selected or "", and part of the business
		String pkey = request.getParameter("pkey");
		if(pkey==null) {
			return mapping.findForward("credit-card-manager");
		}
		CreditCard creditCard;
		if(pkey.length()==0) {
			creditCard=null;
		} else {
			creditCard = aoConn.getCreditCards().get(Integer.parseInt(pkey));
			if(creditCard==null) return mapping.findForward("credit-card-manager");
			if(!creditCard.getBusiness().equals(business)) throw new SQLException("Requested business and CreditCard business do not match: "+creditCard.getBusiness().getAccounting()+"!="+business.getAccounting());
		}

		business.setUseMonthlyCreditCard(creditCard);

		// Store request attributes
		request.setAttribute("business", business);
		request.setAttribute("creditCard", creditCard);

		return mapping.findForward("success");
	}

	private static final List<Permission.Name> permissions;
	static {
		List<Permission.Name> newList = new ArrayList<Permission.Name>(2);
		newList.add(Permission.Name.get_credit_cards);
		newList.add(Permission.Name.edit_credit_card);
		permissions = Collections.unmodifiableList(newList);
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return permissions;
	}
}
