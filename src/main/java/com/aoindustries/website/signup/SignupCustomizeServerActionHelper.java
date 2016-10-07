/*
 * Copyright 2007-2009, 2015, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.PackageDefinition;
import com.aoindustries.aoserv.client.PackageDefinitionLimit;
import com.aoindustries.aoserv.client.Resource;
import com.aoindustries.encoding.ChainWriter;
import com.aoindustries.website.SiteSettings;
import static com.aoindustries.website.signup.ApplicationResources.accessor;
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
final public class SignupCustomizeServerActionHelper {

	/**
	 * Make no instances.
	 */
	private SignupCustomizeServerActionHelper() {}

	public static void setRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		SignupSelectPackageForm signupSelectPackageForm,
		SignupCustomizeServerForm signupCustomizeServerForm
	) throws IOException, SQLException {
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getPackageDefinitions().get(signupSelectPackageForm.getPackageDefinition());
		if(packageDefinition==null) throw new SQLException("Unable to find PackageDefinition: "+signupSelectPackageForm.getPackageDefinition());
		List<PackageDefinitionLimit> limits = packageDefinition.getLimits();

		// Find the cheapest resources to scale prices from
		int maxPowers = 0;
		PackageDefinitionLimit cheapestPower = null;
		int maxCPUs = 0;
		PackageDefinitionLimit cheapestCPU = null;
		int maxRAMs = 0;
		PackageDefinitionLimit cheapestRAM = null;
		int maxSataControllers = 0;
		PackageDefinitionLimit cheapestSataController = null;
		int maxScsiControllers = 0;
		PackageDefinitionLimit cheapestScsiController = null;
		int maxDisks = 0;
		PackageDefinitionLimit cheapestDisk = null;
		for(PackageDefinitionLimit limit : limits) {
			String resourceName = limit.getResource().getName();
			if(resourceName.startsWith("hardware_power_")) {
				int limitPower = limit.getHardLimit();
				if(limitPower>0) {
					if(limitPower>maxPowers) maxPowers = limitPower;
					if(cheapestPower==null) cheapestPower = limit;
					else {
						BigDecimal additionalRate = limit.getAdditionalRate();
						if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
						BigDecimal cheapestRate = cheapestPower.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						if(additionalRate.compareTo(cheapestRate)<0) cheapestPower = limit;
					}
				}
			} else if(resourceName.startsWith("hardware_processor_")) {
				int limitCpu = limit.getHardLimit();
				if(limitCpu>0) {
					if(limitCpu>maxCPUs) maxCPUs = limitCpu;
					if(cheapestCPU==null) cheapestCPU = limit;
					else {
						BigDecimal additionalRate = limit.getAdditionalRate();
						if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
						BigDecimal cheapestRate = cheapestCPU.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						if(additionalRate.compareTo(cheapestRate)<0) cheapestCPU = limit;
					}
				}
			} else if(resourceName.startsWith("hardware_ram_")) {
				int limitRAM = limit.getHardLimit();
				if(limitRAM>0) {
					if(limitRAM>maxRAMs) maxRAMs = limitRAM;
					if(cheapestRAM==null) cheapestRAM = limit;
					else {
						BigDecimal additionalRate = limit.getAdditionalRate();
						if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
						BigDecimal cheapestRate = cheapestRAM.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						if(additionalRate.compareTo(cheapestRate)<0) cheapestRAM = limit;
					}
				}
			} else if(resourceName.startsWith("hardware_disk_controller_sata_")) {
				int limitSataController = limit.getHardLimit();
				if(limitSataController>0) {
					if(limitSataController>maxSataControllers) maxSataControllers = limitSataController;
					if(cheapestSataController==null) cheapestSataController = limit;
					else {
						BigDecimal additionalRate = limit.getAdditionalRate();
						if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
						BigDecimal cheapestRate = cheapestSataController.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						if(additionalRate.compareTo(cheapestRate)<0) cheapestSataController = limit;
					}
				}
			} else if(resourceName.startsWith("hardware_disk_controller_scsi_")) {
				int limitScsiController = limit.getHardLimit();
				if(limitScsiController>0) {
					if(limitScsiController>maxScsiControllers) maxScsiControllers = limitScsiController;
					if(cheapestScsiController==null) cheapestScsiController = limit;
					else {
						BigDecimal additionalRate = limit.getAdditionalRate();
						if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
						BigDecimal cheapestRate = cheapestScsiController.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						if(additionalRate.compareTo(cheapestRate)<0) cheapestScsiController = limit;
					}
				}
			} else if(resourceName.startsWith("hardware_disk_")) {
				int hardLimit = limit.getHardLimit();
				if(hardLimit>0) {
					if(cheapestDisk==null) cheapestDisk = limit;
					else {
						BigDecimal additionalRate = limit.getAdditionalRate();
						if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
						BigDecimal cheapestRate = cheapestDisk.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						if(additionalRate.compareTo(cheapestRate)<0) cheapestDisk = limit;
					}
					if(hardLimit>maxDisks) maxDisks = hardLimit;
				}
			}
		}
		if(cheapestCPU==null) throw new SQLException("Unable to find cheapestCPU");
		if(cheapestRAM==null) throw new SQLException("Unable to find cheapestRAM");
		if(cheapestDisk==null) throw new SQLException("Unable to find cheapestDisk");

		// Find all the options
		List<Option> powerOptions = new ArrayList<Option>();
		List<Option> cpuOptions = new ArrayList<Option>();
		List<Option> ramOptions = new ArrayList<Option>();
		List<Option> sataControllerOptions = new ArrayList<Option>();
		List<Option> scsiControllerOptions = new ArrayList<Option>();
		List<List<Option>> diskOptions = new ArrayList<List<Option>>();
		for(int c=0;c<maxDisks;c++) diskOptions.add(new ArrayList<Option>());
		for(PackageDefinitionLimit limit : limits) {
			Resource resource = limit.getResource();
			String resourceName = resource.getName();
			if(resourceName.startsWith("hardware_power_")) {
				int limitPower = limit.getHardLimit();
				if(limitPower>0) {
					assert cheapestPower != null;
					BigDecimal additionalRate = limit.getAdditionalRate();
					if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
					BigDecimal cheapestRate = cheapestPower.getAdditionalRate();
					if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
					String description = maxPowers==1 ? resource.toString() : (maxPowers+"x"+resource.toString());
					powerOptions.add(new Option(limit.getPkey(), description, BigDecimal.valueOf(maxPowers).multiply(additionalRate.subtract(cheapestRate))));
				}
			} else if(resourceName.startsWith("hardware_processor_")) {
				int limitCpu = limit.getHardLimit();
				if(limitCpu>0) {
					BigDecimal additionalRate = limit.getAdditionalRate();
					if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
					BigDecimal cheapestRate = cheapestCPU.getAdditionalRate();
					if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
					String description = maxCPUs==1 ? resource.toString() : (maxCPUs+"x"+resource.toString());
					cpuOptions.add(new Option(limit.getPkey(), description, BigDecimal.valueOf(maxCPUs).multiply(additionalRate.subtract(cheapestRate))));
				}
			} else if(resourceName.startsWith("hardware_ram_")) {
				int limitRAM = limit.getHardLimit();
				if(limitRAM>0) {
					BigDecimal additionalRate = limit.getAdditionalRate();
					if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
					BigDecimal cheapestRate = cheapestRAM.getAdditionalRate();
					if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
					String description = maxRAMs==1 ? resource.toString() : (maxRAMs+"x"+resource.toString());
					ramOptions.add(new Option(limit.getPkey(), description, BigDecimal.valueOf(maxRAMs).multiply(additionalRate.subtract(cheapestRate))));
				}
			} else if(resourceName.startsWith("hardware_disk_controller_sata_")) {
				int limitSataController = limit.getHardLimit();
				if(limitSataController>0) {
					assert cheapestSataController != null;
					BigDecimal additionalRate = limit.getAdditionalRate();
					if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
					BigDecimal cheapestRate = cheapestSataController.getAdditionalRate();
					if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
					String description = maxSataControllers==1 ? resource.toString() : (maxSataControllers+"x"+resource.toString());
					sataControllerOptions.add(new Option(limit.getPkey(), description, BigDecimal.valueOf(maxSataControllers).multiply(additionalRate.subtract(cheapestRate))));
				}
			} else if(resourceName.startsWith("hardware_disk_controller_scsi_")) {
				int limitScsiController = limit.getHardLimit();
				if(limitScsiController>0) {
					assert cheapestScsiController != null;
					BigDecimal additionalRate = limit.getAdditionalRate();
					if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
					BigDecimal cheapestRate = cheapestScsiController.getAdditionalRate();
					if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
					String description = maxScsiControllers==1 ? resource.toString() : (maxScsiControllers+"x"+resource.toString());
					scsiControllerOptions.add(new Option(limit.getPkey(), description, BigDecimal.valueOf(maxScsiControllers).multiply(additionalRate.subtract(cheapestRate))));
				}
			} else if(resourceName.startsWith("hardware_disk_")) {
				int limitDisk = limit.getHardLimit();
				if(limitDisk>0) {
					BigDecimal additionalRate = limit.getAdditionalRate();
					if(additionalRate==null) additionalRate=BigDecimal.valueOf(0, 2);
					BigDecimal adjustedRate = additionalRate;
					// Discount adjusted rate if the cheapest disk is of this type
					if(cheapestDisk.getResource().getName().startsWith("hardware_disk_")) {
						BigDecimal cheapestRate = cheapestDisk.getAdditionalRate();
						if(cheapestRate==null) cheapestRate=BigDecimal.valueOf(0, 2);
						adjustedRate = adjustedRate.subtract(cheapestRate);
					}
					for(int c=0;c<maxDisks;c++) {
						List<Option> options = diskOptions.get(c);
						// Add none option
						if(maxDisks>1 && options.isEmpty()) options.add(new Option(-1, "None", c==0 ? adjustedRate.subtract(additionalRate) : BigDecimal.valueOf(0, 2)));
						options.add(new Option(limit.getPkey(), resource.toString(), c==0 ? adjustedRate : additionalRate));
					}
				}
			}
		}

		// Sort by price
		Collections.sort(powerOptions, new Option.PriceComparator());
		Collections.sort(cpuOptions, new Option.PriceComparator());
		Collections.sort(ramOptions, new Option.PriceComparator());
		Collections.sort(sataControllerOptions, new Option.PriceComparator());
		Collections.sort(scsiControllerOptions, new Option.PriceComparator());
		for(List<Option> diskOptionList : diskOptions) Collections.sort(diskOptionList, new Option.PriceComparator());

		// Clear any customization settings that are not part of the current package definition (this happens when they
		// select a different package type)
		if(signupCustomizeServerForm.getPowerOption()!=-1) {
			PackageDefinitionLimit pdl = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getPowerOption());
			if(pdl==null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeServerForm.setPowerOption(-1);
		}
		if(signupCustomizeServerForm.getCpuOption()!=-1) {
			PackageDefinitionLimit pdl = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getCpuOption());
			if(pdl==null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeServerForm.setCpuOption(-1);
		}
		if(signupCustomizeServerForm.getRamOption()!=-1) {
			PackageDefinitionLimit pdl = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getRamOption());
			if(pdl==null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeServerForm.setRamOption(-1);
		}
		if(signupCustomizeServerForm.getSataControllerOption()!=-1) {
			PackageDefinitionLimit pdl = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getSataControllerOption());
			if(pdl==null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeServerForm.setSataControllerOption(-1);
		}
		if(signupCustomizeServerForm.getScsiControllerOption()!=-1) {
			PackageDefinitionLimit pdl = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getScsiControllerOption());
			if(pdl==null || !packageDefinition.equals(pdl.getPackageDefinition())) signupCustomizeServerForm.setScsiControllerOption(-1);
		}
		List<String> formDiskOptions = signupCustomizeServerForm.getDiskOptions();
		while(formDiskOptions.size()>maxDisks) formDiskOptions.remove(formDiskOptions.size()-1);
		for(int c=0;c<formDiskOptions.size();c++) {
			String S = formDiskOptions.get(c);
			if(S!=null && S.length()>0 && !S.equals("-1")) {
				int pkey = Integer.parseInt(S);
				PackageDefinitionLimit pdl = rootConn.getPackageDefinitionLimits().get(pkey);
				if(pdl==null || !packageDefinition.equals(pdl.getPackageDefinition())) formDiskOptions.set(c, "-1");
			}
		}

		// Determine if at least one disk is selected
		boolean isAtLeastOneDiskSelected = signupCustomizeServerForm.isAtLeastOneDiskSelected();

		// Default to cheapest if not already selected
		if(cheapestPower!=null && signupCustomizeServerForm.getPowerOption()==-1) signupCustomizeServerForm.setPowerOption(cheapestPower.getPkey());
		if(signupCustomizeServerForm.getCpuOption()==-1) signupCustomizeServerForm.setCpuOption(cheapestCPU.getPkey());
		if(signupCustomizeServerForm.getRamOption()==-1) signupCustomizeServerForm.setRamOption(cheapestRAM.getPkey());
		if(cheapestSataController!=null && signupCustomizeServerForm.getSataControllerOption()==-1) signupCustomizeServerForm.setSataControllerOption(cheapestSataController.getPkey());
		if(cheapestScsiController!=null && signupCustomizeServerForm.getScsiControllerOption()==-1) signupCustomizeServerForm.setScsiControllerOption(cheapestScsiController.getPkey());
		for(int c=0;c<maxDisks;c++) {
			List<Option> options = diskOptions.get(c);
			if(!options.isEmpty()) {
				Option firstOption = options.get(0);
				if(!isAtLeastOneDiskSelected && options.size()>=2 && firstOption.getPriceDifference().compareTo(BigDecimal.ZERO)<0) {
					firstOption = options.get(1);
				}
				String defaultSelected = Integer.toString(firstOption.getPackageDefinitionLimit());
				if(formDiskOptions.size()<=c || formDiskOptions.get(c)==null || formDiskOptions.get(c).length()==0 || formDiskOptions.get(c).equals("-1")) formDiskOptions.set(c, defaultSelected);
			} else {
				formDiskOptions.set(c, "-1");
			}
		}

		// Find the basePrice (base plus minimum number of cheapest of each resource class)
		BigDecimal basePrice = packageDefinition.getMonthlyRate();
		if(basePrice==null) basePrice = BigDecimal.valueOf(0, 2);
		if(cheapestPower!=null && cheapestPower.getAdditionalRate()!=null) basePrice = basePrice.add(cheapestPower.getAdditionalRate().multiply(BigDecimal.valueOf(maxPowers)));
		if(cheapestCPU.getAdditionalRate()!=null) basePrice = basePrice.add(cheapestCPU.getAdditionalRate().multiply(BigDecimal.valueOf(maxCPUs)));
		if(cheapestRAM.getAdditionalRate()!=null) basePrice = basePrice.add(cheapestRAM.getAdditionalRate());
		if(cheapestSataController!=null && cheapestSataController.getAdditionalRate()!=null) basePrice = basePrice.add(cheapestSataController.getAdditionalRate());
		if(cheapestScsiController!=null && cheapestScsiController.getAdditionalRate()!=null) basePrice = basePrice.add(cheapestScsiController.getAdditionalRate());
		if(cheapestDisk.getAdditionalRate()!=null) basePrice = basePrice.add(cheapestDisk.getAdditionalRate());

		// Store to request
		request.setAttribute("packageDefinition", packageDefinition);
		request.setAttribute("powerOptions", powerOptions);
		request.setAttribute("cpuOptions", cpuOptions);
		request.setAttribute("ramOptions", ramOptions);
		request.setAttribute("sataControllerOptions", sataControllerOptions);
		request.setAttribute("scsiControllerOptions", scsiControllerOptions);
		request.setAttribute("diskOptions", diskOptions);
		request.setAttribute("basePrice", basePrice);
	}

	/**
	 * Gets the hardware monthly rate for the server, basic server + hardware options
	 */
	public static BigDecimal getHardwareMonthlyRate(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm, PackageDefinition packageDefinition) throws IOException, SQLException {
		BigDecimal monthlyRate = packageDefinition.getMonthlyRate();

		// Add the power option
		int powerOption = signupCustomizeServerForm.getPowerOption();
		if(powerOption!=-1) {
			PackageDefinitionLimit powerPDL = rootConn.getPackageDefinitionLimits().get(powerOption);
			int numPower = powerPDL.getHardLimit();
			BigDecimal powerRate = powerPDL.getAdditionalRate();
			if(powerRate!=null) monthlyRate = monthlyRate.add(BigDecimal.valueOf(numPower).multiply(powerRate));
		}

		// Add the cpu option
		PackageDefinitionLimit cpuPDL = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getCpuOption());
		int numCpu = cpuPDL.getHardLimit();
		BigDecimal cpuRate = cpuPDL.getAdditionalRate();
		if(cpuRate!=null) monthlyRate = monthlyRate.add(BigDecimal.valueOf(numCpu).multiply(cpuRate));

		// Add the RAM option
		PackageDefinitionLimit ramPDL = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getRamOption());
		int numRam = ramPDL.getHardLimit();
		BigDecimal ramRate = ramPDL.getAdditionalRate();
		if(ramRate!=null) monthlyRate = monthlyRate.add(BigDecimal.valueOf(numRam).multiply(ramRate));

		// Add the SATA controller option
		int sataControllerOption = signupCustomizeServerForm.getSataControllerOption();
		if(sataControllerOption!=-1) {
			PackageDefinitionLimit sataControllerPDL = rootConn.getPackageDefinitionLimits().get(sataControllerOption);
			int numSataController = sataControllerPDL.getHardLimit();
			BigDecimal sataControllerRate = sataControllerPDL.getAdditionalRate();
			if(sataControllerRate!=null) monthlyRate = monthlyRate.add(BigDecimal.valueOf(numSataController).multiply(sataControllerRate));
		}

		// Add the SCSI controller option
		int scsiControllerOption = signupCustomizeServerForm.getScsiControllerOption();
		if(scsiControllerOption!=-1) {
			PackageDefinitionLimit scsiControllerPDL = rootConn.getPackageDefinitionLimits().get(scsiControllerOption);
			int numScsiController = scsiControllerPDL.getHardLimit();
			BigDecimal scsiControllerRate = scsiControllerPDL.getAdditionalRate();
			if(scsiControllerRate!=null) monthlyRate = monthlyRate.add(BigDecimal.valueOf(numScsiController).multiply(scsiControllerRate));
		}

		// Add the disk options
		for(String pkey : signupCustomizeServerForm.getDiskOptions()) {
			if(pkey!=null && pkey.length()>0 && !pkey.equals("-1")) {
				PackageDefinitionLimit diskPDL = rootConn.getPackageDefinitionLimits().get(Integer.parseInt(pkey));
				if(diskPDL!=null) {
					BigDecimal diskRate = diskPDL.getAdditionalRate();
					if(diskRate!=null) monthlyRate = monthlyRate.add(diskRate);
				}
			}
		}

		return monthlyRate;
	}

	public static String getPowerOption(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		int powerOption = signupCustomizeServerForm.getPowerOption();
		if(powerOption==-1) return null;
		PackageDefinitionLimit powerPDL = rootConn.getPackageDefinitionLimits().get(powerOption);
		int numPower = powerPDL.getHardLimit();
		if(numPower==1) return powerPDL.getResource().toString();
		else return numPower + "x" + powerPDL.getResource().toString();
	}

	public static String getCpuOption(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		PackageDefinitionLimit cpuPDL = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getCpuOption());
		int numCpu = cpuPDL.getHardLimit();
		if(numCpu==1) return cpuPDL.getResource().toString(); //.replaceAll(", ", "<br />&#160;&#160;&#160;&#160;");
		else return numCpu + "x" + cpuPDL.getResource().toString(); //.replaceAll(", ", "<br />&#160;&#160;&#160;&#160;");
	}

	public static String getRamOption(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		PackageDefinitionLimit ramPDL = rootConn.getPackageDefinitionLimits().get(signupCustomizeServerForm.getRamOption());
		int numRam = ramPDL.getHardLimit();
		if(numRam==1) return ramPDL.getResource().toString();
		else return numRam + "x" + ramPDL.getResource().toString();
	}

	public static String getSataControllerOption(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		int sataControllerOption = signupCustomizeServerForm.getSataControllerOption();
		if(sataControllerOption==-1) return null;
		PackageDefinitionLimit sataControllerPDL = rootConn.getPackageDefinitionLimits().get(sataControllerOption);
		int numSataController = sataControllerPDL.getHardLimit();
		if(numSataController==1) return sataControllerPDL.getResource().toString();
		else return numSataController + "x" + sataControllerPDL.getResource().toString();
	}

	public static String getScsiControllerOption(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		int scsiControllerOption = signupCustomizeServerForm.getScsiControllerOption();
		if(scsiControllerOption==-1) return null;
		PackageDefinitionLimit scsiControllerPDL = rootConn.getPackageDefinitionLimits().get(scsiControllerOption);
		int numScsiController = scsiControllerPDL.getHardLimit();
		if(numScsiController==1) return scsiControllerPDL.getResource().toString();
		else return numScsiController + "x" + scsiControllerPDL.getResource().toString();
	}

	public static List<String> getDiskOptions(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		List<String> diskOptions = new ArrayList<String>();
		for(String pkey : signupCustomizeServerForm.getDiskOptions()) {
			if(pkey!=null && pkey.length()>0 && !pkey.equals("-1")) {
				PackageDefinitionLimit diskPDL = rootConn.getPackageDefinitionLimits().get(Integer.parseInt(pkey));
				if(diskPDL!=null) diskOptions.add(diskPDL.getResource().toString());
			}
		}
		return diskOptions;
	}

	public static void setConfirmationRequestAttributes(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		SignupSelectPackageForm signupSelectPackageForm,
		SignupCustomizeServerForm signupCustomizeServerForm
	) throws IOException, SQLException {
		// Lookup things needed by the view
		AOServConnector rootConn = SiteSettings.getInstance(servletContext).getRootAOServConnector();
		PackageDefinition packageDefinition = rootConn.getPackageDefinitions().get(signupSelectPackageForm.getPackageDefinition());

		// Store as request attribute for the view
		request.setAttribute("monthlyRate", getHardwareMonthlyRate(rootConn, signupCustomizeServerForm, packageDefinition));
		request.setAttribute("powerOption", getPowerOption(rootConn, signupCustomizeServerForm));
		request.setAttribute("cpuOption", getCpuOption(rootConn, signupCustomizeServerForm));
		request.setAttribute("ramOption", getRamOption(rootConn, signupCustomizeServerForm));
		request.setAttribute("sataControllerOption", getSataControllerOption(rootConn, signupCustomizeServerForm));
		request.setAttribute("scsiControllerOption", getScsiControllerOption(rootConn, signupCustomizeServerForm));
		request.setAttribute("diskOptions", getDiskOptions(rootConn, signupCustomizeServerForm));
	}

	public static void printConfirmation(
		HttpServletRequest request,
		ChainWriter emailOut,
		AOServConnector rootConn,
		PackageDefinition packageDefinition,
		SignupCustomizeServerForm signupCustomizeServerForm
	) throws IOException, SQLException {
		String powerOption = getPowerOption(rootConn, signupCustomizeServerForm);
		if(!GenericValidator.isBlankOrNull(powerOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
						 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.power.prompt")).print("</td>\n"
						 + "        <td>").print(powerOption).print("</td>\n"
						 + "    </tr>\n");
		}
		emailOut.print("    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.cpu.prompt")).print("</td>\n"
					 + "        <td>").print(getCpuOption(rootConn, signupCustomizeServerForm)).print("</td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.ram.prompt")).print("</td>\n"
					 + "        <td>").encodeXhtml(getRamOption(rootConn, signupCustomizeServerForm)).print("</td>\n"
					 + "    </tr>\n");
		String sataControllerOption = getSataControllerOption(rootConn, signupCustomizeServerForm);
		if(!GenericValidator.isBlankOrNull(sataControllerOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
						 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.sataController.prompt")).print("</td>\n"
						 + "        <td>").print(sataControllerOption).print("</td>\n"
						 + "    </tr>\n");
		}
		String scsiControllerOption = getScsiControllerOption(rootConn, signupCustomizeServerForm);
		if(!GenericValidator.isBlankOrNull(scsiControllerOption)) {
			emailOut.print("    <tr>\n"
						 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
						 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.scsiController.prompt")).print("</td>\n"
						 + "        <td>").print(scsiControllerOption).print("</td>\n"
						 + "    </tr>\n");
		}
		for(String diskOption : getDiskOptions(rootConn, signupCustomizeServerForm)) {
			emailOut.print("    <tr>\n"
						 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
						 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.disk.prompt")).print("</td>\n"
						 + "        <td>").encodeXhtml(diskOption).print("</td>\n"
						 + "    </tr>\n");
		}
		emailOut.print("    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td>").print(accessor.getMessage("signupCustomizeServerConfirmation.setup.prompt")).print("</td>\n"
					 + "        <td>\n");
		BigDecimal setup = packageDefinition.getSetupFee();
		if(setup==null) {
			emailOut.print("            ").print(accessor.getMessage("signupCustomizeServerConfirmation.setup.none")).print("\n");
		} else {
			emailOut.print("            $").print(setup).print("\n");
		}
		emailOut.print("        </td>\n"
					 + "    </tr>\n"
					 + "    <tr>\n"
					 + "        <td>").print(accessor.getMessage("signup.notRequired")).print("</td>\n"
					 + "        <td style='white-space:nowrap'>").print(accessor.getMessage("signupCustomizeServerConfirmation.monthlyRate.prompt")).print("</td>\n"
					 + "        <td>$").print(request.getAttribute("monthlyRate")).print("</td>\n"
					 + "    </tr>\n");
	}

	/**
	 * Gets the total amount of hard drive space in gigabytes.
	 */
	public static int getTotalHardwareDiskSpace(AOServConnector rootConn, SignupCustomizeServerForm signupCustomizeServerForm) throws IOException, SQLException {
		if(signupCustomizeServerForm==null) return 0;
		int total = 0;
		for(String diskOption : signupCustomizeServerForm.getDiskOptions()) {
			if(diskOption!=null && diskOption.length()>0 && !"-1".equals(diskOption)) {
				PackageDefinitionLimit limit = rootConn.getPackageDefinitionLimits().get(Integer.parseInt(diskOption));
				if(limit!=null) {
					Resource resource = limit.getResource();
					String name = resource.getName();
					if(name.startsWith("hardware_disk_")) {
						// Is in formation hardware_disk_RPM_SIZE
						int pos = name.indexOf('_', 14);
						if(pos!=-1) {
							int pos2 = name.indexOf('_', pos+1);
							if(pos2==-1) {
								// Not raid
								total += Integer.parseInt(name.substring(pos+1));
							} else {
								// Does it end with _raid1, double space if it does.
								if(name.endsWith("_raid1")) {
									total += 2*Integer.parseInt(name.substring(pos+1, pos2));
								} else {
									total += Integer.parseInt(name.substring(pos+1, pos2));
								}
							}
						}
					}
				}
			}
		}
		return total;
	}
}
