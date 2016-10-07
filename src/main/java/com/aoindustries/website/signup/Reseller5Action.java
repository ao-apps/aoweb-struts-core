/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
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
public class Reseller5Action extends ResellerStepAction {

	@Override
	public ActionForward executeResellerStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		ResellerSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		SignupBusinessForm signupBusinessForm,
		boolean signupBusinessFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception {
		if(!signupSelectPackageFormComplete) return mapping.findForward("reseller-completed");
		if(!signupBusinessFormComplete) return mapping.findForward("reseller-2-completed");
		if(!signupTechnicalFormComplete) return mapping.findForward("reseller-3-completed");
		if(!signupBillingInformationFormComplete) return mapping.findForward("reseller-4-completed");

		initRequestAttributes(
			request,
			response,
			signupSelectPackageForm,
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
		SignupBusinessForm signupBusinessForm,
		SignupTechnicalForm signupTechnicalForm,
		SignupBillingInformationForm signupBillingInformationForm
	) throws IOException, SQLException {
		ServletContext servletContext = getServlet().getServletContext();

		SignupSelectPackageActionHelper.setConfirmationRequestAttributes(servletContext, request, signupSelectPackageForm);
		SignupBusinessActionHelper.setConfirmationRequestAttributes(servletContext, request, signupBusinessForm);
		SignupTechnicalActionHelper.setConfirmationRequestAttributes(servletContext, request, signupTechnicalForm);
		SignupBillingInformationActionHelper.setConfirmationRequestAttributes(servletContext, request, signupBillingInformationForm);
	}
}
