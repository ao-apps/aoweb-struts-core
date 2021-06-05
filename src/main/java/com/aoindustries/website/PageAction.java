/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.website;

import com.aoapps.web.resources.registry.Registry;
import com.aoapps.web.resources.servlet.PageServlet;
import com.aoapps.web.resources.servlet.RegistryEE;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * An action servlet that creates a {@link com.aoapps.web.resources.servlet.RegistryEE.Page page-scope web resource registry},
 * if not already present.
 *
 * @see  PageServlet
 */
abstract public class PageAction extends Action {

	/**
	 * Creates the page-scope registry, if not already present, then invokes
	 * {@link #execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, com.aoapps.web.resources.registry.Registry)}.
	 * The registry if left on the request to be available to any forwarding target.
	 */
	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response
	) throws Exception {
		Registry pageRegistry = RegistryEE.Page.get(request);
		if(pageRegistry == null) {
			// Create a new page-scope registry
			pageRegistry = new Registry();
			RegistryEE.Page.set(request, pageRegistry);
		}
		return execute(mapping, form, request, response, pageRegistry);
	}

	/**
	 * Once the page registry is set resolved, this version of the execute method is invoked.
	 */
	abstract public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		Registry pageRegistry
	) throws Exception;
}
