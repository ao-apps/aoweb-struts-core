/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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
public class Backup5Action extends BackupStepAction {

	@Override
	public ActionForward executeBackupStep(
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
	) throws Exception {
		if(!signupSelectPackageFormComplete) return mapping.findForward("backup-completed");
		if(!signupBusinessFormComplete) return mapping.findForward("backup-2-completed");
		if(!signupTechnicalFormComplete) return mapping.findForward("backup-3-completed");
		if(!signupBillingInformationFormComplete) return mapping.findForward("backup-4-completed");

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
