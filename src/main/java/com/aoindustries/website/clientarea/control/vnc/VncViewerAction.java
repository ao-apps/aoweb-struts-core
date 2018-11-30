/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016, 2018  AO Industries, Inc.
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
package com.aoindustries.website.clientarea.control.vnc;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.infrastructure.VirtualServer;
import com.aoindustries.aoserv.client.master.Permission;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.website.PermissionAction;
import com.aoindustries.website.SiteSettings;
import com.aoindustries.website.Skin;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Finds the virtualServer and sets request attribute "virtualServer" if accessible.
 *
 * @author  AO Industries, Inc.
 */
public class VncViewerAction extends PermissionAction {

	@Override
	public ActionForward executePermissionGranted(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		try {
			VirtualServer virtualServer = aoConn.getVirtualServers().get(Integer.parseInt(request.getParameter("virtualServer")));
			if(virtualServer==null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			String vncPassword = virtualServer.getVncPassword();
			if(vncPassword==null) {
				// VNC disabled
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			if(vncPassword.equals(AoservProtocol.FILTERED)) {
				// Not accessible
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
			request.setAttribute("virtualServer", virtualServer);
			return mapping.findForward("success");
		} catch(NumberFormatException err) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}

	@Override
	public List<Permission.Name> getPermissions() {
		return Collections.singletonList(Permission.Name.vnc_console);
	}
}
