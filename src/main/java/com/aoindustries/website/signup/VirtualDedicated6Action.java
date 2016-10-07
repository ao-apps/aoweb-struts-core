/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author  AO Industries, Inc.
 */
public class VirtualDedicated6Action extends VirtualDedicatedStepAction {

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
		SignupBusinessForm signupBusinessForm,
		boolean signupBusinessFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception {
		if(!signupSelectPackageFormComplete) return mapping.findForward("virtual-dedicated-server-completed");
		if(!signupCustomizeServerFormComplete) return mapping.findForward("virtual-dedicated-server-2-completed");
		if(!signupBusinessFormComplete) return mapping.findForward("virtual-dedicated-server-3-completed");
		if(!signupTechnicalFormComplete) return mapping.findForward("virtual-dedicated-server-4-completed");
		if(!signupBillingInformationFormComplete) return mapping.findForward("virtual-dedicated-server-5-completed");

		initRequestAttributes(
			request,
			response,
			signupSelectPackageForm,
			signupCustomizeServerForm,
			signupBusinessForm,
			signupTechnicalForm,
			signupBillingInformationForm
		);

		return mapping.findForward("input");
	}

	protected void initRequestAttributes(
		HttpServletRequest request,
		HttpServletResponse response,
		SignupSelectPackageForm signupSelectPackageForm,
		SignupCustomizeServerForm signupCustomizeServerForm,
		SignupBusinessForm signupBusinessForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) throws IOException, SQLException {
		ServletContext servletContext = getServlet().getServletContext();

		SignupSelectServerActionHelper.setConfirmationRequestAttributes(servletContext, request, signupSelectPackageForm);
		SignupCustomizeServerActionHelper.setConfirmationRequestAttributes(servletContext, request, response, signupSelectPackageForm, signupCustomizeServerForm);
		SignupBusinessActionHelper.setConfirmationRequestAttributes(servletContext, request, signupBusinessForm);
		SignupTechnicalActionHelper.setConfirmationRequestAttributes(servletContext, request, signupTechnicalForm);
		SignupBillingInformationActionHelper.setConfirmationRequestAttributes(servletContext, request, signupBillingInformationForm);
	}
}
