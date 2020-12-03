/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2015, 2016, 2018, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.html.Html;
import com.aoindustries.website.SiteSettings;
import static com.aoindustries.website.signup.Resources.RESOURCES;
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
final public class SignupSelectServerActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupSelectServerActionHelper() {}

	public static void setRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		String packageCategoryName
	) throws IOException, SQLException {
		List<Host> servers = getServers(servletContext, packageCategoryName);

		request.setAttribute("servers", servers);
	}

	/**
	 * Gets the possible servers ordered by minimum monthly rate.
	 */
	public static List<Host> getServers(ServletContext servletContext, String packageCategoryName) throws IOException, SQLException {
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageCategory category = rootConn.getBilling().getPackageCategory().get(packageCategoryName);
		Account rootAccount = rootConn.getCurrentAdministrator().getUsername().getPackage().getAccount();
		List<PackageDefinition> packageDefinitions = rootAccount.getPackageDefinitions(category);
		List<Host> servers = new ArrayList<>();

		for(PackageDefinition packageDefinition : packageDefinitions) {
			if(packageDefinition.isActive()) {
				servers.add(
					new Host(
						ServerConfiguration.getMinimumConfiguration(packageDefinition),
						ServerConfiguration.getMaximumConfiguration(packageDefinition)
					)
				);
			}
		}

		Collections.sort(servers, new ServerComparator());

		return servers;
	}

	public static class Host {
		final private ServerConfiguration minimumConfiguration;
		final private ServerConfiguration maximumConfiguration;

		private Host(
			ServerConfiguration minimumConfiguration,
			ServerConfiguration maximumConfiguration
		) {
			this.minimumConfiguration = minimumConfiguration;
			this.maximumConfiguration = maximumConfiguration;
		}

		public ServerConfiguration getMinimumConfiguration() {
			return minimumConfiguration;
		}

		public ServerConfiguration getMaximumConfiguration() {
			return maximumConfiguration;
		}
	}

	private static class ServerComparator implements Comparator<Host> {
		@Override
		public int compare(Host s1, Host s2) {
			return s1.getMinimumConfiguration().getMonthly().compareTo(s2.getMinimumConfiguration().getMonthly());
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

	public static void printConfirmation(ChainWriter emailOut, Html html, PackageDefinition packageDefinition) throws IOException {
		emailOut.print("    <tr>\n"
					 + "        <td>");
		html.text(RESOURCES.getMessage("signup.notRequired"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(RESOURCES.getMessage("signupSelectServerForm.packageDefinition.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").textInXhtml(packageDefinition.getDisplay()).print("</td>\n"
					 + "    </tr>\n");
	}
}
