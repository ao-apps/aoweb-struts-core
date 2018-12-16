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
import com.aoindustries.website.AuthenticatedAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.ArrayList;
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
		Account thisBusiness = aoConn.getThisBusinessAdministrator().getUsername().getPackage().getBusiness();

		// Get the list of businesses that are not canceled or have a non-zero balance, or are thisBusiness
		List<Account> allBusinesses = aoConn.getAccount().getBusinesses().getRows();
		List<Account> businesses = new ArrayList<Account>(allBusinesses.size());
		for(Account business : allBusinesses) {
			if(
				thisBusiness.equals(business)
				|| (
					business.getCanceled()==null
					&& !business.billParent()
				) || business.getAccountBalance().signum()!=0
			) businesses.add(business);
		}
		if(businesses.size()==1) {
			// Redirect, only one option
			response.sendRedirect(response.encodeRedirectURL(skin.getUrlBase(request)+"clientarea/accounting/make-payment-select-card.do?accounting="+businesses.get(0).getAccounting()));
			return null;
		} else {
			// Show selector screen
			request.setAttribute("businesses", businesses);
			return mapping.findForward("success");
		}
	}
}
