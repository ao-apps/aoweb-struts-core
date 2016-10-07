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
public class SystemAdministrationAction extends SystemAdministrationStepAction {

	@Override
	public ActionForward executeSystemAdministrationStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		SystemAdministrationSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		SignupBusinessForm signupBusinessForm,
		boolean signupBusinessFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception {
		List<PackageDefinition> packageDefinitions = SignupSelectPackageActionHelper.getPackageDefinitions(getServlet().getServletContext(), PackageCategory.SYSADMIN);
		if(packageDefinitions.size()==1) {
			response.sendRedirect(
				response.encodeRedirectURL(
					skin.getUrlBase(request)
					+"signup/system-administration-completed.do?packageDefinition="
					+packageDefinitions.get(0).getPkey()
				)
			);
			return null;
		}
		SignupSelectPackageActionHelper.setRequestAttributes(getServlet().getServletContext(), request, response, PackageCategory.SYSADMIN);

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
