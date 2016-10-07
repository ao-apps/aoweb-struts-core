/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.password;

import com.aoindustries.aoserv.client.PasswordGenerator;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import com.aoindustries.website.SkinAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Generates a list of passwords, stores in request attribute "generatedPasswords", and forwards to "success".
 *
 * @author  AO Industries, Inc.
 */
public class PasswordGeneratorAction extends SkinAction {

	private static final int NUM_PASSWORDS = 10;

	@Override
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin
	) throws Exception {
		// Generate the passwords
		List<String> generatedPasswords = new ArrayList<String>(NUM_PASSWORDS);
		for(int c=0;c<10;c++) generatedPasswords.add(PasswordGenerator.generatePassword());

		// Set request values
		request.setAttribute("generatedPasswords", generatedPasswords);

		return mapping.findForward("success");
	}
}
