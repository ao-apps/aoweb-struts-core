/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.AOServPermission;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.sql.SQLException;
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
 * Makes sure the authenticated user has the necessary permissions to perform the requested task.
 * If they do not, sets the request attribute "permissionDenied" with the <code>List&lt;AOServConnector&gt;</code> and returns mapping for "permissionDenied".
 * Otherwise, if all the permissions have been granted, calls <code>executePermissionGranted</code>.
 *
 * The default implementation of this new <code>executePermissionGranted</code> method simply returns the mapping
 * of "success".
 *
 * @author  AO Industries, Inc.
 */
abstract public class PermissionAction extends AuthenticatedAction {

	@Override
	final public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn
	) throws Exception {
		List<AOServPermission.Permission> permissions = getPermissions();

		// No permissions defined, default to denied
		if(permissions==null || permissions.isEmpty()) {
			List<AOServPermission> aoPerms = Collections.emptyList();
			return executePermissionDenied(
				mapping,
				form,
				request,
				response,
				siteSettings,
				locale,
				skin,
				aoConn,
				aoPerms
			);
		}

		BusinessAdministrator thisBA = aoConn.getThisBusinessAdministrator();
		// Return denied on first missing permission
		for(AOServPermission.Permission permission : permissions) {
			if(!thisBA.hasPermission(permission)) {
				List<AOServPermission> aoPerms = new ArrayList<AOServPermission>(permissions.size());
				for(AOServPermission.Permission requiredPermission : permissions) {
					AOServPermission aoPerm = aoConn.getAoservPermissions().get(requiredPermission);
					if(aoPerm==null) throw new SQLException("Unable to find AOServPermission: "+requiredPermission);
					aoPerms.add(aoPerm);
				}
				return executePermissionDenied(
					mapping,
					form,
					request,
					response,
					siteSettings,
					locale,
					skin,
					aoConn,
					aoPerms
				);
			}
		}

		// All permissions found, consider granted
		return executePermissionGranted(mapping, form, request, response, siteSettings, locale, skin, aoConn);
	}

	/**
	 * Called when permission has been granted.  By default,
	 * returns mapping for "success".
	 */
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
		return mapping.findForward("success");
	}

	/**
	 * Called when the permissions has been denied.  By default,
	 * sets request attribute <code>Constants.PERMISSION_DENIED</code>
	 * and returns mapping for "permission-denied".
	 */
	public ActionForward executePermissionDenied(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response,
		SiteSettings siteSettings,
		Locale locale,
		Skin skin,
		AOServConnector aoConn,
		List<AOServPermission> permissions
	) throws Exception {
		request.setAttribute(Constants.PERMISSION_DENIED, permissions);
		return mapping.findForward("permission-denied");
	}

	/**
	 * Gets the list of permissions that are required for this action.  Returning a null or empty list will result in nothing being allowed.
	 *
	 * @see  AOServPermission
	 */
	abstract public List<AOServPermission.Permission> getPermissions();
}
