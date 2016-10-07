/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.clientarea.control.vnc;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.AOServProtocol;
import com.aoindustries.aoserv.client.VirtualServer;
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
			if(vncPassword.equals(AOServProtocol.FILTERED)) {
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
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.vnc_console);
	}
}
