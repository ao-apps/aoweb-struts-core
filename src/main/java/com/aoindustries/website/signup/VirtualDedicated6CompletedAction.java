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
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;

/**
 * @author  AO Industries, Inc.
 */
public class VirtualDedicated6CompletedAction extends VirtualDedicated6Action {

	@Override
	public ActionForward executeVirtualDedicatedStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		VirtualDedicatedSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		VirtualDedicatedSignupCustomizeServerForm signupCustomizeServerForm,
		boolean signupCustomizeServerFormComplete,
		SignupOrganizationForm signupOrganizationForm,
		boolean signupOrganizationFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception {
		// Forward to previous steps if they have not been completed
		if(!signupSelectPackageFormComplete) return mapping.findForward("virtual-dedicated-server-completed");
		if(!signupCustomizeServerFormComplete) return mapping.findForward("virtual-dedicated-server-2-completed");
		if(!signupOrganizationFormComplete) return mapping.findForward("virtual-dedicated-server-3-completed");
		if(!signupTechnicalFormComplete) return mapping.findForward("virtual-dedicated-server-4-completed");
		if(!signupBillingInformationFormComplete) return mapping.findForward("virtual-dedicated-server-5-completed");

		// Let the parent class do the initialization of the request attributes for both the emails and the final JSP
		initRequestAttributes(
			request,
			response,
			signupSelectPackageForm,
			signupCustomizeServerForm,
			signupOrganizationForm,
			signupTechnicalForm,
			signupBillingInformationForm
		);

		// Used later
		HttpSession session = request.getSession();
		ActionServlet myServlet = getServlet();
		ServletContext servletContext = myServlet.getServletContext();
		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getBilling().getPackageDefinition().get(signupSelectPackageForm.getPackageDefinition());

		// Build the options map
		Map<String,String> options = new HashMap<>();
		ServerConfirmationCompletedActionHelper.addOptions(options, signupCustomizeServerForm);

		// Store to the database
		ServerConfirmationCompletedActionHelper.storeToDatabase(myServlet, request, rootConn, packageDefinition, signupOrganizationForm, signupTechnicalForm, signupBillingInformationForm, options);
		String pkey = (String)request.getAttribute("pkey");
		String statusKey = (String)request.getAttribute("statusKey");

		// Send confirmation email to support
		ServerConfirmationCompletedActionHelper.sendSupportSummaryEmail(
			myServlet,
			request,
			pkey,
			statusKey,
			siteSettings,
			packageDefinition,
			signupCustomizeServerForm,
			null,
			signupOrganizationForm,
			signupTechnicalForm,
			signupBillingInformationForm
		);

		// Send confirmation email to customer
		ServerConfirmationCompletedActionHelper.sendCustomerSummaryEmails(
			myServlet,
			request,
			pkey,
			statusKey,
			siteSettings,
			packageDefinition,
			signupCustomizeServerForm,
			null,
			signupOrganizationForm,
			signupTechnicalForm,
			signupBillingInformationForm
		);

		// Clear virtualDedicated signup-specific forms from the session
		session.removeAttribute("virtualDedicatedSignupSelectPackageForm");
		session.removeAttribute("virtualDedicatedSignupCustomizeServerForm");

		return mapping.findForward("success");
	}
}
