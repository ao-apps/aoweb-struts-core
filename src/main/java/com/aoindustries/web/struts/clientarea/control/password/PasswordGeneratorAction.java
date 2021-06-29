/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.web.struts.clientarea.control.password;

import com.aoindustries.aoserv.client.password.PasswordGenerator;
import com.aoindustries.web.struts.SiteSettings;
import com.aoindustries.web.struts.Skin;
import com.aoindustries.web.struts.SkinAction;
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
		List<String> generatedPasswords = new ArrayList<>(NUM_PASSWORDS);
		for(int c=0;c<10;c++) generatedPasswords.add(PasswordGenerator.generatePassword());

		// Set request values
		request.setAttribute("generatedPasswords", generatedPasswords);

		return mapping.findForward("success");
	}
}
