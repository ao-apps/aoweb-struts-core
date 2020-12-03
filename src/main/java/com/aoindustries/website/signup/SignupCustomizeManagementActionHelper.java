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
import com.aoindustries.aoserv.client.billing.PackageDefinition;
import com.aoindustries.aoserv.client.billing.PackageDefinitionLimit;
import com.aoindustries.aoserv.client.billing.Resource;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.html.Html;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.util.i18n.Monies;
import com.aoindustries.website.SiteSettings;
import static com.aoindustries.website.signup.Resources.RESOURCES;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.validator.GenericValidator;

/**
 * Managed2Action and Dedicated2Action both use this to setup the request attributes.  This is implemented
 * here because inheritance is not possible and neither one is logically above the other.
 *
 * @author  AO Industries, Inc.
 */
final public class SignupCustomizeManagementActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupCustomizeManagementActionHelper() {}

	public static void setRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		SignupSelectPackageForm signupSelectPackageForm,
		SignupCustomizeServerForm signupCustomizeServerForm,
		SignupCustomizeManagementForm signupCustomizeManagementForm
	) throws IOException, SQLException {
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getBilling().getPackageDefinition().get(signupSelectPackageForm.getPackageDefinition());
		if(packageDefinition == null) throw new SQLException("Unable to find PackageDefinition: " + signupSelectPackageForm.getPackageDefinition());
		List<PackageDefinitionLimit> limits = packageDefinition.getLimits();

		// Get the total harddrive space in gigabytes
		int totalHardwareDiskSpace = SignupCustomizeServerActionHelper.getTotalHardwareDiskSpace(rootConn, signupCustomizeServerForm);

		// Find all the options
		List<Option> backupOnsiteOptions = new ArrayList<>();
		List<Option> backupOffsiteOptions = new ArrayList<>();
		List<Option> distributionScanOptions = new ArrayList<>();
		List<Option> failoverOptions = new ArrayList<>();
		for(PackageDefinitionLimit limit : limits) {
			Resource resource = limit.getResource();
			String resourceName = resource.getName();
			if(resourceName.startsWith("backup_onsite_")) {
				int limitPower = limit.getHardLimit();
				if(limitPower==PackageDefinitionLimit.UNLIMITED || limitPower>0) {
					// This is per gigabyte of physical space
					Monies additionalRate = Monies.of(limit.getAdditionalRate());
					backupOnsiteOptions.add(new Option(limit.getPkey(), resource.toString(), additionalRate.multiply(BigDecimal.valueOf(totalHardwareDiskSpace))));
				}
			} else if(resourceName.startsWith("backup_offsite_")) {
				int limitPower = limit.getHardLimit();
				if(limitPower==PackageDefinitionLimit.UNLIMITED || limitPower>0) {
					// This is per gigabyte of physical space
					Monies additionalRate = Monies.of(limit.getAdditionalRate());
					backupOffsiteOptions.add(new Option(limit.getPkey(), resource.toString(), additionalRate.multiply(BigDecimal.valueOf(totalHardwareDiskSpace))));
				}
			}
		}
		// Distribution scan option
		{
			Resource resource = rootConn.getBilling().getResource().get(Resource.DISTRIBUTION_SCAN);
			if(resource == null) {
				servletContext.log(null, new SQLException("Unable to find Resource: " + Resource.DISTRIBUTION_SCAN));
			} else {
				PackageDefinitionLimit limit = packageDefinition.getLimit(resource);
				if(limit != null) {
					int hard = limit.getHardLimit();
					if(hard==PackageDefinitionLimit.UNLIMITED || hard>0) {
						Monies additionalRate = Monies.of(limit.getAdditionalRate());
						distributionScanOptions.add(new Option(limit.getPkey(), resource.toString(), additionalRate));
					}
				}
			}
		}
		// Failover option
		{
			Resource resource = rootConn.getBilling().getResource().get(Resource.FAILOVER);
			if(resource == null) {
				servletContext.log(null, new SQLException("Unable to find Resource: " + Resource.FAILOVER));
			} else {
				PackageDefinitionLimit limit = packageDefinition.getLimit(resource);
				if(limit != null) {
					int hard = limit.getHardLimit();
					if(hard==PackageDefinitionLimit.UNLIMITED || hard>0) {
						// This is per gigabyte of physical space
						Monies additionalRate = Monies.of(limit.getAdditionalRate());
						additionalRate = additionalRate.multiply(BigDecimal.valueOf(totalHardwareDiskSpace));
						failoverOptions.add(new Option(limit.getPkey(), resource.toString(), additionalRate));

						// Only once the failover option is available will the MySQL replication option be available
						Resource mrResource = rootConn.getBilling().getResource().get(Resource.MYSQL_REPLICATION);
						if(mrResource == null) {
							servletContext.log(null, new SQLException("Unable to find Resource: " + Resource.MYSQL_REPLICATION));
						} else {
							PackageDefinitionLimit mrLimit = packageDefinition.getLimit(mrResource);
							if(mrLimit != null) {
								int mrHard = mrLimit.getHardLimit();
								if(mrHard==PackageDefinitionLimit.UNLIMITED || mrHard>0) {
									Monies mrAdditionalRate = Monies.of(mrLimit.getAdditionalRate());
									failoverOptions.add(new Option(mrLimit.getPkey(), mrResource.toString(), additionalRate.add(mrAdditionalRate)));
								}
							}
						}
					}
				}
			}
		}

		if(!backupOnsiteOptions.isEmpty()) backupOnsiteOptions.add(0, new Option(-1, "No On-Site Backup", Monies.of()));
		if(!backupOffsiteOptions.isEmpty()) backupOffsiteOptions.add(0, new Option(-1, "No Off-Site Backup", Monies.of()));
		if(!distributionScanOptions.isEmpty()) distributionScanOptions.add(0, new Option(-1, "No daily scans", Monies.of()));
		if(!failoverOptions.isEmpty()) failoverOptions.add(0, new Option(-1, "No Fail-Over Mirror", Monies.of()));

		// Sort by price
		Collections.sort(backupOnsiteOptions, Option.priceComparator);
		Collections.sort(backupOffsiteOptions, Option.priceComparator);
		Collections.sort(distributionScanOptions, Option.priceComparator);
		Collections.sort(failoverOptions, Option.priceComparator);

		// Clear any customization settings that are not part of the current package definition (this happens when they
		// select a different package type)
		if(signupCustomizeManagementForm.getBackupOnsiteOption() != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(signupCustomizeManagementForm.getBackupOnsiteOption());
			if(pdl == null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeManagementForm.setBackupOnsiteOption(-1);
		}
		if(signupCustomizeManagementForm.getBackupOffsiteOption() != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(signupCustomizeManagementForm.getBackupOffsiteOption());
			if(pdl == null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeManagementForm.setBackupOffsiteOption(-1);
		}
		if(signupCustomizeManagementForm.getDistributionScanOption() != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(signupCustomizeManagementForm.getDistributionScanOption());
			if(pdl == null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeManagementForm.setDistributionScanOption(-1);
		}
		if(signupCustomizeManagementForm.getFailoverOption() != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(signupCustomizeManagementForm.getFailoverOption());
			if(pdl == null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeManagementForm.setFailoverOption(-1);
		}

		// Store to request
		request.setAttribute("packageDefinition", packageDefinition);
		request.setAttribute("hardwareRate", SignupCustomizeServerActionHelper.getHardwareMonthlyRate(rootConn, signupCustomizeServerForm, packageDefinition));
		request.setAttribute("backupOnsiteOptions", backupOnsiteOptions);
		request.setAttribute("backupOffsiteOptions", backupOffsiteOptions);
		request.setAttribute("distributionScanOptions", distributionScanOptions);
		request.setAttribute("failoverOptions", failoverOptions);
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		SignupSelectPackageForm signupSelectPackageForm,
		SignupCustomizeServerForm signupCustomizeServerForm,
		SignupCustomizeManagementForm signupCustomizeManagementForm
	) throws IOException, SQLException {
		// Lookup things needed by the view
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getBilling().getPackageDefinition().get(signupSelectPackageForm.getPackageDefinition());

		// Store as request attribute for the view
		request.setAttribute("totalMonthlyRate", getTotalMonthlyRate(rootConn, signupCustomizeServerForm, signupCustomizeManagementForm, packageDefinition));
		request.setAttribute("backupOnsiteOption", getBackupOnsiteOption(rootConn, signupCustomizeManagementForm));
		request.setAttribute("backupOffsiteOption", getBackupOffsiteOption(rootConn, signupCustomizeManagementForm));
		request.setAttribute("backupDvdOption", getBackupDvdOption(rootConn, signupCustomizeManagementForm));
		request.setAttribute("distributionScanOption", getDistributionScanOption(rootConn, signupCustomizeManagementForm));
		request.setAttribute("failoverOption", getFailoverOption(rootConn, signupCustomizeManagementForm));
	}

	public static void printConfirmation(
		HttpServletRequest request,
		ChainWriter emailOut,
		Html html,
		AOServConnector rootConn,
		SignupCustomizeManagementForm signupCustomizeManagementForm
	) throws IOException, SQLException {
		String backupOnsiteOption = getBackupOnsiteOption(rootConn, signupCustomizeManagementForm);
		if(!GenericValidator.isBlankOrNull(backupOnsiteOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signup.notRequired"));
			emailOut.print("</td>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signupCustomizeManagementConfirmation.backupOnsite.prompt"));
			emailOut.print("</td>\n"
						 + "        <td>").print(backupOnsiteOption).print("</td>\n"
						 + "    </tr>\n");
		}
		String backupOffsiteOption = getBackupOffsiteOption(rootConn, signupCustomizeManagementForm);
		if(!GenericValidator.isBlankOrNull(backupOffsiteOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signup.notRequired"));
			emailOut.print("</td>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signupCustomizeManagementConfirmation.backupOffsite.prompt"));
			emailOut.print("</td>\n"
						 + "        <td>").print(backupOffsiteOption).print("</td>\n"
						 + "    </tr>\n");
		}
		String backupDvdOption = getBackupDvdOption(rootConn, signupCustomizeManagementForm);
		if(!GenericValidator.isBlankOrNull(backupDvdOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signup.notRequired"));
			emailOut.print("</td>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signupCustomizeManagementConfirmation.backupDvd.prompt"));
			emailOut.print("</td>\n"
						 + "        <td>").print(backupDvdOption).print("</td>\n"
						 + "    </tr>\n");
		}
		String distributionScanOption = getDistributionScanOption(rootConn, signupCustomizeManagementForm);
		if(!GenericValidator.isBlankOrNull(distributionScanOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signup.notRequired"));
			emailOut.print("</td>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signupCustomizeManagementConfirmation.distributionScan.prompt"));
			emailOut.print("</td>\n"
						 + "        <td>").print(distributionScanOption).print("</td>\n"
						 + "    </tr>\n");
		}
		String failoverOption = getFailoverOption(rootConn, signupCustomizeManagementForm);
		if(!GenericValidator.isBlankOrNull(failoverOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signup.notRequired"));
			emailOut.print("</td>\n"
						 + "        <td>");
			html.text(RESOURCES.getMessage("signupCustomizeManagementConfirmation.failover.prompt"));
			emailOut.print("</td>\n"
						 + "        <td>").print(failoverOption).print("</td>\n"
						 + "    </tr>\n");
		}
		emailOut.print("    <tr>\n"
					 + "        <td>");
		html.text(RESOURCES.getMessage("signup.notRequired"));
		emailOut.print("</td>\n"
					 + "        <td>");
		html.text(RESOURCES.getMessage("signupCustomizeManagementConfirmation.totalMonthlyRate.prompt"));
		emailOut.print("</td>\n"
					 + "        <td>").print(request.getAttribute("totalMonthlyRate")).print("</td>\n"
					 + "    </tr>\n");
	}

	/**
	 * Gets the total monthly rate for the server, basic server + hardware options + management options
	 */
	public static Monies getTotalMonthlyRate(
		AOServConnector rootConn,
		SignupCustomizeServerForm signupCustomizeServerForm,
		SignupCustomizeManagementForm signupCustomizeManagementForm,
		PackageDefinition packageDefinition
	) throws SQLException, IOException {
		Monies monthlyRate = SignupCustomizeServerActionHelper.getHardwareMonthlyRate(rootConn, signupCustomizeServerForm, packageDefinition);

		int totalDiskSpace = SignupCustomizeServerActionHelper.getTotalHardwareDiskSpace(rootConn, signupCustomizeServerForm);

		// Add the backup onsite option
		int backupOnsiteOption = signupCustomizeManagementForm.getBackupOnsiteOption();
		if(backupOnsiteOption != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(backupOnsiteOption);
			Money rate = pdl.getAdditionalRate();
			if(rate != null) monthlyRate = monthlyRate.add(rate.multiply(BigDecimal.valueOf(totalDiskSpace)));
		}

		// Add the backup offsite option
		int backupOffsiteOption = signupCustomizeManagementForm.getBackupOffsiteOption();
		if(backupOffsiteOption != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(backupOffsiteOption);
			Money rate = pdl.getAdditionalRate();
			if(rate != null) monthlyRate = monthlyRate.add(rate.multiply(BigDecimal.valueOf(totalDiskSpace)));
		}

		// Add the distributionScanOption option
		int distributionScanOption = signupCustomizeManagementForm.getDistributionScanOption();
		if(distributionScanOption != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(distributionScanOption);
			monthlyRate = monthlyRate.add(pdl.getAdditionalRate());
		}

		// Add the backup offsite option
		int failoverOption = signupCustomizeManagementForm.getFailoverOption();
		if(failoverOption != -1) {
			PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(failoverOption);
			String resourceName = pdl.getResource().getName();
			if(Resource.FAILOVER.equals(resourceName)) {
				// Failover mirror only
				Money rate = pdl.getAdditionalRate();
				if(rate != null) monthlyRate = monthlyRate.add(rate.multiply(BigDecimal.valueOf(totalDiskSpace)));
			} else if(Resource.MYSQL_REPLICATION.equals(resourceName)) {
				// Failover mirror plus MySQL replication
				Resource failoverResource = rootConn.getBilling().getResource().get(Resource.FAILOVER);
				if(failoverResource == null) throw new SQLException("Unable to find Resource: " + Resource.FAILOVER);
				PackageDefinitionLimit failoverPDL = packageDefinition.getLimit(failoverResource);
				if(failoverPDL == null) throw new SQLException("Unable to find PackageDefinitionLimit: "+Resource.FAILOVER+" on PackageDefinition #"+packageDefinition.getPkey());
				Monies additionalRate = Monies.of();
				Money failoverRate = failoverPDL.getAdditionalRate();
				if(failoverRate != null) additionalRate = additionalRate.add(failoverRate.multiply(BigDecimal.valueOf(totalDiskSpace)));
				additionalRate = additionalRate.add(pdl.getAdditionalRate());
				monthlyRate = monthlyRate.add(additionalRate);
			}
		}

		return monthlyRate;
	}

	public static String getBackupOnsiteOption(AOServConnector rootConn, SignupCustomizeManagementForm signupCustomizeManagementForm) throws IOException, SQLException {
		int option = signupCustomizeManagementForm.getBackupOnsiteOption();
		if(option == -1) return null;
		PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(option);
		return pdl.getResource().toString();
	}

	public static String getBackupOffsiteOption(AOServConnector rootConn, SignupCustomizeManagementForm signupCustomizeManagementForm) throws IOException, SQLException {
		int option = signupCustomizeManagementForm.getBackupOffsiteOption();
		if(option == -1) return null;
		PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(option);
		return pdl.getResource().toString();
	}

	public static String getBackupDvdOption(AOServConnector rootConn, SignupCustomizeManagementForm signupCustomizeManagementForm) {
		String option = signupCustomizeManagementForm.getBackupDvdOption();
		if(option == null || option.length() == 0) return null;
		return option;
	}

	public static String getDistributionScanOption(AOServConnector rootConn, SignupCustomizeManagementForm signupCustomizeManagementForm) throws SQLException, IOException {
		int option = signupCustomizeManagementForm.getDistributionScanOption();
		if(option == -1) return null;
		PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(option);
		return pdl.getResource().toString();
	}

	public static String getFailoverOption(AOServConnector rootConn, SignupCustomizeManagementForm signupCustomizeManagementForm) throws IOException, SQLException {
		int option = signupCustomizeManagementForm.getFailoverOption();
		if(option == -1) return null;
		PackageDefinitionLimit pdl = rootConn.getBilling().getPackageDefinitionLimit().get(option);
		return pdl.getResource().toString();
	}
}
