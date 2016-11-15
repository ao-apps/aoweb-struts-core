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
package com.aoindustries.website.signup;

import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author  AO Industries, Inc.
 */
public class VirtualDedicated2CompletedAction extends VirtualDedicated2Action {

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
		if(!signupCustomizeServerFormComplete) {
			// Init values for the form
			return super.executeVirtualDedicatedStep(
				mapping,
				request,
				response,
				siteSettings,
				locale,
				skin,
				signupSelectPackageForm,
				signupSelectPackageFormComplete,
				signupCustomizeServerForm,
				signupCustomizeServerFormComplete,
				signupBusinessForm,
				signupBusinessFormComplete,
				signupTechnicalForm,
				signupTechnicalFormComplete,
				signupBillingInformationForm,
				signupBillingInformationFormComplete
			);
		}
		if(!signupBusinessFormComplete) return mapping.findForward("virtual-dedicated-server-3");
		if(!signupTechnicalFormComplete) return mapping.findForward("virtual-dedicated-server-4");
		if(!signupBillingInformationFormComplete) return mapping.findForward("virtual-dedicated-server-5");
		return mapping.findForward("virtual-dedicated-server-6");
	}

	/**
	 * Errors are not cleared for the complete step.
	 */
	@Override
	protected void clearErrors(HttpServletRequest req) {
		// Do nothing
	}
}
