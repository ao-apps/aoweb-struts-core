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
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.billing.PackageCategory;
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.website.SiteSettings;
import static com.aoindustries.website.signup.ApplicationResources.accessor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ManagedAction and DedicatedAction both use this to setup the request attributes.  This is implemented
 * here because inheritance is not possible and neither one is logically above the other.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupSelectPackageActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupSelectPackageActionHelper() {}

	public static void setRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		String packageCategoryName
	) throws IOException, SQLException {
		List<PackageDefinition> packageDefinitions = getPackageDefinitions(servletContext, packageCategoryName);

		request.setAttribute("packageDefinitions", packageDefinitions);
	}

	/**
	 * Gets the active package definitions ordered by monthly rate.
	 */
	public static List<PackageDefinition> getPackageDefinitions(ServletContext servletContext, String packageCategoryName) throws IOException, SQLException {
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageCategory category = rootConn.getBilling().getPackageCategory().get(packageCategoryName);
		Account rootBusiness = rootConn.getThisBusinessAdministrator().getUsername().getPackage().getBusiness();
		List<PackageDefinition> packageDefinitions = rootBusiness.getPackageDefinitions(category);
		List<PackageDefinition> activePackageDefinitions = new ArrayList<>();

		for(PackageDefinition packageDefinition : packageDefinitions) {
			if(packageDefinition.isActive()) activePackageDefinitions.add(packageDefinition);
		}

		Collections.sort(activePackageDefinitions, new PackageDefinitionComparator());

		return activePackageDefinitions;
	}

	private static class PackageDefinitionComparator implements Comparator<PackageDefinition> {
		@Override
		public int compare(PackageDefinition pd1, PackageDefinition pd2) {
			return pd1.getMonthlyRate().compareTo(pd2.getMonthlyRate());
		}
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		SignupSelectPackageForm signupSelectPackageForm
	) throws IOException, SQLException {
		// Lookup things needed by the view
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getBilling().getPackageDefinition().get(signupSelectPackageForm.getPackageDefinition());

		// Store as request attribute for the view
		request.setAttribute("packageDefinition", packageDefinition);
		request.setAttribute("setup", packageDefinition.getSetupFee());
	}

	public static void printConfirmation(ChainWriter emailOut, PackageDefinition packageDefinition) throws IOException {
		emailOut.print("    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupSelectPackageForm.packageDefinition.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(packageDefinition.getDisplay()).print("</td>\n"
					 + "    </tr>\n");
	}
}
