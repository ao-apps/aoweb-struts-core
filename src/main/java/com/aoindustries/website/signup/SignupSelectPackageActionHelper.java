/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.PackageCategory;
import com.aoindustries.aoserv.client.PackageDefinition;
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
		PackageCategory category = rootConn.getPackageCategories().get(packageCategoryName);
		Business rootBusiness = rootConn.getThisBusinessAdministrator().getUsername().getPackage().getBusiness();
		List<PackageDefinition> packageDefinitions = rootBusiness.getPackageDefinitions(category);
		List<PackageDefinition> activePackageDefinitions = new ArrayList<PackageDefinition>();

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
		PackageDefinition packageDefinition = rootConn.getPackageDefinitions().get(signupSelectPackageForm.getPackageDefinition());

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
