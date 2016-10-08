/*
 * Copyright 2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.PackageCategory;
import com.aoindustries.aoserv.client.PackageDefinition;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * @author  AO Industries, Inc.
 */
public class VirtualHostingAction extends VirtualHostingStepAction {

	@Override
	public ActionForward executeVirtualHostingStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		VirtualHostingSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		SignupDomainForm signupDomainForm,
		boolean signupDomainFormComplete,
		SignupBusinessForm signupBusinessForm,
		boolean signupBusinessFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception {
		List<PackageDefinition> packageDefinitions = SignupSelectPackageActionHelper.getPackageDefinitions(getServlet().getServletContext(), PackageCategory.VIRTUAL);
		if(packageDefinitions.size()==1) {
			response.sendRedirect(
				response.encodeRedirectURL(
					skin.getUrlBase(request)
					+"signup/virtual-hosting-completed.do?packageDefinition="
					+packageDefinitions.get(0).getPkey()
				)
			);
			return null;
		}
		SignupSelectPackageActionHelper.setRequestAttributes(getServlet().getServletContext(), request, response, PackageCategory.VIRTUAL);

		// Clear errors if they should not be displayed
		clearErrors(request);

		return mapping.findForward("input");
	}

	/**
	 * May clear specific errors here.
	 */
	protected void clearErrors(HttpServletRequest request) {
		saveErrors(request, new ActionMessages());
	}
}