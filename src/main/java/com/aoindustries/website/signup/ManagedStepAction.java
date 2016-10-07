/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
		SignupBusinessForm signupBusinessForm = SignupHelper.getSessionActionForm(servlet, session, SignupBusinessForm.class, "signupBusinessForm");
		SignupTechnicalForm signupTechnicalForm = SignupHelper.getSessionActionForm(servlet, session, SignupTechnicalForm.class, "signupTechnicalForm");
		SignupBillingInformationForm signupBillingInformationForm = SignupHelper.getSessionActionForm(servlet, session, SignupBillingInformationForm.class, "signupBillingInformationForm");

		ActionMessages signupSelectPackageFormErrors = signupSelectPackageForm.validate(mapping, request);
		ActionMessages signupCustomizeServerFormErrors = signupCustomizeServerForm.validate(mapping, request);
		ActionMessages signupCustomizeManagementFormErrors = signupCustomizeManagementForm.validate(mapping, request);
		ActionMessages signupBusinessFormErrors = signupBusinessForm.validate(mapping, request);
		ActionMessages signupTechnicalFormErrors = signupTechnicalForm.validate(mapping, request);
		ActionMessages signupBillingInformationFormErrors = signupBillingInformationForm.validate(mapping, request);

		boolean signupSelectPackageFormComplete = !doAddErrors(request, signupSelectPackageFormErrors);
		boolean signupCustomizeServerFormComplete = !doAddErrors(request, signupCustomizeServerFormErrors);
		boolean signupCustomizeManagementFormComplete;
		if(doAddErrors(request, signupCustomizeManagementFormErrors)) signupCustomizeManagementFormComplete = false;
		else if(!"true".equals(signupCustomizeManagementForm.getFormCompleted())) signupCustomizeManagementFormComplete = false;
		else signupCustomizeManagementFormComplete = true;
		boolean signupBusinessFormComplete = !doAddErrors(request, signupBusinessFormErrors);
		boolean signupTechnicalFormComplete = !doAddErrors(request, signupTechnicalFormErrors);
		boolean signupBillingInformationFormComplete = !doAddErrors(request, signupBillingInformationFormErrors);

		request.setAttribute("signupSelectPackageFormComplete", signupSelectPackageFormComplete ? "true" : "false");
		request.setAttribute("signupCustomizeServerFormComplete", signupCustomizeServerFormComplete ? "true" : "false");
		request.setAttribute("signupCustomizeManagementFormComplete", signupCustomizeManagementFormComplete ? "true" : "false");
		request.setAttribute("signupBusinessFormComplete", signupBusinessFormComplete ? "true" : "false");
		request.setAttribute("signupTechnicalFormComplete", signupTechnicalFormComplete ? "true" : "false");
		request.setAttribute("signupBillingInformationFormComplete", signupBillingInformationFormComplete ? "true" : "false");

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
			signupBusinessForm,
			signupBusinessFormComplete,
			signupTechnicalForm,
			signupTechnicalFormComplete,
			signupBillingInformationForm,
			signupBillingInformationFormComplete
		);
	}

	/**
	 * Clears checkboxes when not in form.
	 */
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
		SignupBusinessForm signupBusinessForm,
		boolean signupBusinessFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception;
}
