/*
 * Copyright 2009, 2015, 2016 by AO Industries, Inc.,
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
abstract public class BackupStepAction extends SkinAction {

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
				"backup".equals(selectedStep)
				|| "backup-2".equals(selectedStep)
				|| "backup-3".equals(selectedStep)
				|| "backup-4".equals(selectedStep)
				|| "backup-5".equals(selectedStep)
			) {
				return mapping.findForward(selectedStep);
			}
		}

		HttpSession session = request.getSession();

		BackupSignupSelectPackageForm signupSelectPackageForm = SignupHelper.getSessionActionForm(servlet, session, BackupSignupSelectPackageForm.class, "backupSignupSelectPackageForm");
		SignupBusinessForm signupBusinessForm = SignupHelper.getSessionActionForm(servlet, session, SignupBusinessForm.class, "signupBusinessForm");
		SignupTechnicalForm signupTechnicalForm = SignupHelper.getSessionActionForm(servlet, session, SignupTechnicalForm.class, "signupTechnicalForm");
		SignupBillingInformationForm signupBillingInformationForm = SignupHelper.getSessionActionForm(servlet, session, SignupBillingInformationForm.class, "signupBillingInformationForm");

		ActionMessages signupSelectPackageFormErrors = signupSelectPackageForm.validate(mapping, request);
		ActionMessages signupBusinessFormErrors = signupBusinessForm.validate(mapping, request);
		ActionMessages signupTechnicalFormErrors = signupTechnicalForm.validate(mapping, request);
		ActionMessages signupBillingInformationFormErrors = signupBillingInformationForm.validate(mapping, request);

		boolean signupSelectPackageFormComplete = !doAddErrors(request, signupSelectPackageFormErrors);
		boolean signupBusinessFormComplete = !doAddErrors(request, signupBusinessFormErrors);
		boolean signupTechnicalFormComplete = !doAddErrors(request, signupTechnicalFormErrors);
		boolean signupBillingInformationFormComplete = !doAddErrors(request, signupBillingInformationFormErrors);

		request.setAttribute("signupSelectPackageFormComplete", signupSelectPackageFormComplete ? "true" : "false");
		request.setAttribute("signupBusinessFormComplete", signupBusinessFormComplete ? "true" : "false");
		request.setAttribute("signupTechnicalFormComplete", signupTechnicalFormComplete ? "true" : "false");
		request.setAttribute("signupBillingInformationFormComplete", signupBillingInformationFormComplete ? "true" : "false");

		return executeBackupStep(
			mapping,
			request,
			response,
			siteSettings,
			locale,
			skin,
			signupSelectPackageForm,
			signupSelectPackageFormComplete,
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

	public abstract ActionForward executeBackupStep(
		ActionMapping mapping,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		BackupSignupSelectPackageForm signupSelectPackageForm,
		boolean signupSelectPackageFormComplete,
		SignupBusinessForm signupBusinessForm,
		boolean signupBusinessFormComplete,
		SignupTechnicalForm signupTechnicalForm,
		boolean signupTechnicalFormComplete,
		SignupBillingInformationForm signupBillingInformationForm,
		boolean signupBillingInformationFormComplete
	) throws Exception;
}