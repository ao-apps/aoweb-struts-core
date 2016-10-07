/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.accounting;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.CreditCard;
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
		Business business = aoConn.getBusinesses().get(AccountingCode.valueOf(accounting));
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

	private static final List<AOServPermission.Permission> permissions;
	static {
		List<AOServPermission.Permission> newList = new ArrayList<AOServPermission.Permission>(2);
		newList.add(AOServPermission.Permission.get_credit_cards);
		newList.add(AOServPermission.Permission.edit_credit_card);
		permissions = Collections.unmodifiableList(newList);
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return permissions;
	}
}
