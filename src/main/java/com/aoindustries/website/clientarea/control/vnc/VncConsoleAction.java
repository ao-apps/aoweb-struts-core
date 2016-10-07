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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Gets the list of accessible consoles and stores as request attribute <code>vncVirtualServers</code>.
 *
 * @author  AO Industries, Inc.
 */
public class VncConsoleAction extends PermissionAction {

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
		List<VirtualServer> virtualServers = aoConn.getVirtualServers().getRows();
		List<VirtualServer> vncVirtualServers = new ArrayList<VirtualServer>(virtualServers.size());
		for(VirtualServer virtualServer : virtualServers) {
			String vncPassword = virtualServer.getVncPassword();
			if(vncPassword!=null && !vncPassword.equals(AOServProtocol.FILTERED)) vncVirtualServers.add(virtualServer);
		}
		request.setAttribute("vncVirtualServers", vncVirtualServers);

		return mapping.findForward("success");
	}

	@Override
	public List<AOServPermission.Permission> getPermissions() {
		return Collections.singletonList(AOServPermission.Permission.vnc_console);
	}
}
