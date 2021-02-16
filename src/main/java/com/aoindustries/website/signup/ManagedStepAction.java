/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2017, 2019, 2021  AO Industries, Inc.
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
import com.aoindustries.website.SkinAction;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * @author  AO Industries, Inc.
 */
abstract public class ManagedStepAction extends SkinAction {

	/**
	 * Initializes the step details.
	 */
	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin
	) throws Exception {
		// Clear checkboxes that were not part of the request
		clearCheckboxes(request, form);

		// Perform redirect if requested a different step
		String selectedStep = request.getParameter("selectedStep");
		if(selectedStep!=null && (selectedStep=selectedStep.trim()).length()>0) {
			if(
				"managed-server".equals(selectedStep)
				|| "managed-server-2".equals(selectedStep)
				|| "managed-server-3".equals(selectedStep)
				|| "managed-server-4".equals(selectedStep)
				|| "managed-server-5".equals(selectedStep)
				|| "managed-server-6".equals(selectedStep)
				|| "managed-server-7".equals(selectedStep)
			) {
				return mapping.findForward(selectedStep);
			}
		}

		HttpSession session = request.getSession();

		ManagedSignupSelectPackageForm signupSelectPackageForm = SignupHelper.getSessionActionForm(servlet, session, ManagedSignupSelectPackageForm.class, "managedSignupSelectPackageForm");
		ManagedSignupCustomizeServerForm signupCustomizeServerForm = SignupHelper.getSessionActionForm(servlet, session, ManagedSignupCustomizeServerForm.class, "managedSignupCustomizeServerForm");
		SignupCustomizeManagementForm signupCustomizeManagementForm = SignupHelper.getSessionActionForm(servlet, session, SignupCustomizeManagementForm.class, "managedSignupCustomizeManagementForm");
		SignupOrganizationForm signupOrganizationForm = SignupHelper.getSessionActionForm(servlet, session, SignupOrganizationForm.class, "signupOrganizationForm");
		SignupTechnicalForm signupTechnicalForm = SignupHelper.getSessionActionForm(servlet, session, SignupTechnicalForm.class, "signupTechnicalForm");
		SignupBillingInformationForm signupBillingInformationForm = SignupHelper.getSessionActionForm(servlet, session, SignupBillingInformationForm.class, "signupBillingInformationForm");

		ActionMessages signupSelectPackageFormErrors = signupSelectPackageForm.validate(mapping, request);
		ActionMessages signupCustomizeServerFormErrors = signupCustomizeServerForm.validate(mapping, request);
		ActionMessages signupCustomizeManagementFormErrors = signupCustomizeManagementForm.validate(mapping, request);
		ActionMessages signupOrganizationFormErrors = signupOrganizationForm.validate(mapping, request);
		ActionMessages signupTechnicalFormErrors = signupTechnicalForm.validate(mapping, request);
		ActionMessages signupBillingInformationFormErrors = signupBillingInformationForm.validate(mapping, request);

		boolean signupSelectPackageFormComplete = !doAddErrors(request, signupSelectPackageFormErrors);
		boolean signupCustomizeServerFormComplete = !doAddErrors(request, signupCustomizeServerFormErrors);
		boolean signupCustomizeManagementFormComplete;
		if(doAddErrors(request, signupCustomizeManagementFormErrors)) signupCustomizeManagementFormComplete = false;
		else signupCustomizeManagementFormComplete = Boolean.parseBoolean(signupCustomizeManagementForm.getFormCompleted());
		boolean signupOrganizationFormComplete = !doAddErrors(request, signupOrganizationFormErrors);
		boolean signupTechnicalFormComplete = !doAddErrors(request, signupTechnicalFormErrors);
		boolean signupBillingInformationFormComplete = !doAddErrors(request, signupBillingInformationFormErrors);

		request.setAttribute("signupSelectPackageFormComplete", Boolean.toString(signupSelectPackageFormComplete));
		request.setAttribute("signupCustomizeServerFormComplete", Boolean.toString(signupCustomizeServerFormComplete));
		request.setAttribute("signupCustomizeManagementFormComplete", Boolean.toString(signupCustomizeManagementFormComplete));
		request.setAttribute("signupOrganizationFormComplete", Boolean.toString(signupOrganizationFormComplete));
		request.setAttribute("signupTechnicalFormComplete", Boolean.toString(signupTechnicalFormComplete));
		request.setAttribute("signupBillingInformationFormComplete", Boolean.toString(signupBillingInformationFormComplete));

		return executeManagedStep(
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
			signupCustomizeManagementForm,
			signupCustomizeManagementFormComplete,
			signupOrganizationForm,
			signupOrganizationFormComplete,
			signupTechnicalForm,
			signupTechnicalFormComplete,
			signupBillingInformationForm,
			signupBillingInformationFormComplete
		);
	}

	/**
	 * Clears checkboxes when not in form.
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	protected void clearCheckboxes(HttpServletRequest request, ActionForm form) {
		// Do nothing by default
	}

	/**
	 * Saves the provided errors and return <code>true</code> if there were errors to save.
	 */
	private boolean doAddErrors(HttpServletRequest request, ActionMessages errors) {
		if(errors!=null && !errors.isEmpty()) {
			addErrors(request, errors);
			return true;
		}
		return false;
	}

	public abstract ActionForward executeManagedStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		ManagedSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		ManagedSignupCustomizeServerForm signupCustomizeServerForm,
		boolean signupCustomizeServerFormComplete,
		SignupCustomizeManagementForm signupCustomizeManagementForm,
		boolean signupCustomizeManagementFormComplete,
		SignupOrganizationForm signupOrganizationForm,
		boolean signupOrganizationFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception;
}
