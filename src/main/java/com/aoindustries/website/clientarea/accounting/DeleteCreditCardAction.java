/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.CreditCard;
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
 * Prompts if the user really wants to delete a credit card.
 *
 * @author  AO Industries, Inc.
 */
public class DeleteCreditCardAction extends PermissionAction {

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

		// Set request attributes
		request.setAttribute("creditCard", creditCard);

		// Return status success
		return mapping.findForward("success");
	}

	private static final List<AOServPermission.Permission> permissions;
	static {
		List<AOServPermission.Permission> newList = new ArrayList<AOServPermission.Permission>(2);
		newList.add(AOServPermission.Permission.get_credit_cards);
		newList.add(AOServPermission.Permission.delete_credit_card);
		permissions = Collections.unmodifiableList(newList);
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return permissions;
	}
}
