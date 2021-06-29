/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.web.struts.SiteSettings;
import com.aoindustries.web.struts.Skin;
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
public class Dedicated6CompletedAction extends Dedicated6Action {

	@Override
	public ActionForward executeDedicatedStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		DedicatedSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		DedicatedSignupCustomizeServerForm signupCustomizeServerForm,
		boolean signupCustomizeServerFormComplete,
		SignupOrganizationForm signupOrganizationForm,
		boolean signupOrganizationFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception {
		// Forward to previous steps if they have not been completed
		if(!signupSelectPackageFormComplete) return mapping.findForward("dedicated-server-completed");
		if(!signupCustomizeServerFormComplete) return mapping.findForward("dedicated-server-2-completed");
		if(!signupOrganizationFormComplete) return mapping.findForward("dedicated-server-3-completed");
		if(!signupTechnicalFormComplete) return mapping.findForward("dedicated-server-4-completed");
		if(!signupBillingInformationFormComplete) return mapping.findForward("dedicated-server-5-completed");

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
		ActionServlet myServlet = getServlet();
		ServletContext servletContext = myServlet.getServletContext();
		AOServConnector rootConn = siteSettings.getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getBilling().getPackageDefinition().get(signupSelectPackageForm.getPackageDefinition());

		// Build the options map
		Map<String, String> options = new HashMap<>();
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

		// Clear dedicated signup-specific forms from the session
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.removeAttribute("dedicatedSignupSelectPackageForm");
			session.removeAttribute("dedicatedSignupCustomizeServerForm");
		}

		return mapping.findForward("success");
	}
}
