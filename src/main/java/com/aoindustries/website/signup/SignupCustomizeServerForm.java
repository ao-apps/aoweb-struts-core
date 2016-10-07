/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.PackageDefinition;
import com.aoindustries.aoserv.client.PackageDefinitionLimit;
import com.aoindustries.util.AutoGrowArrayList;
import com.aoindustries.util.WrappedException;
import com.aoindustries.website.SessionActionForm;
import com.aoindustries.website.SiteSettings;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionServlet;

/**
 * @author  AO Industries, Inc.
 */
abstract public class SignupCustomizeServerForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private int powerOption;
	private int cpuOption;
	private int ramOption;
	private int sataControllerOption;
	private int scsiControllerOption;
	private List<String> diskOptions;

	public SignupCustomizeServerForm() {
		setPowerOption(-1);
		setCpuOption(-1);
		setRamOption(-1);
		setSataControllerOption(-1);
		setScsiControllerOption(-1);
		setDiskOptions(new AutoGrowArrayList<String>());
	}

	@Override
	public boolean isEmpty() {
		return
			powerOption==-1
			&& cpuOption==-1
			&& ramOption==-1
			&& sataControllerOption==-1
			&& scsiControllerOption==-1
			&& diskOptions.isEmpty()
		;
	}

	public int getPowerOption() {
		return powerOption;
	}

	public void setPowerOption(int powerOption) {
		this.powerOption = powerOption;
	}

	public int getCpuOption() {
		return cpuOption;
	}

	public void setCpuOption(int cpuOption) {
		this.cpuOption = cpuOption;
	}

	public int getRamOption() {
		return ramOption;
	}

	public void setRamOption(int ramOption) {
		this.ramOption = ramOption;
	}

	public int getSataControllerOption() {
		return sataControllerOption;
	}

	public void setSataControllerOption(int sataControllerOption) {
		this.sataControllerOption = sataControllerOption;
	}

	public int getScsiControllerOption() {
		return scsiControllerOption;
	}

	public void setScsiControllerOption(int scsiControllerOption) {
		this.scsiControllerOption = scsiControllerOption;
	}

	public List<String> getDiskOptions() {
		return diskOptions;
	}

	public void setDiskOptions(List<String> diskOptions) {
		this.diskOptions = diskOptions;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if(errors==null) errors = new ActionErrors();
		try {
			ActionServlet myServlet = getServlet();

			// Find the connector
			AOServConnector rootConn;
			if(myServlet!=null) {
				rootConn = SiteSettings.getInstance(myServlet.getServletContext()).getRootAOServConnector();
			} else {
				rootConn = null;
			}

			// Find the current package definition
			PackageDefinition pd = null;
			if(rootConn!=null) {
				SignupSelectPackageForm signupSelectPackageForm = (SignupSelectPackageForm)request.getSession().getAttribute(getSignupSelectPackageFormName());
				if(signupSelectPackageForm!=null) {
					pd = rootConn.getPackageDefinitions().get(signupSelectPackageForm.getPackageDefinition());
				}
			}

			// Find the current limits
			List<PackageDefinitionLimit> limits = pd==null ? null : pd.getLimits();

			if(powerOption==-1 && limits!=null) {
				// Only required when there is at least one power option available
				boolean found = false;
				for(PackageDefinitionLimit limit : limits) {
					if(limit.getResource().getName().startsWith("hardware_power_")) {
						found=true;
						break;
					}
				}
				if(found) errors.add("powerOption", new ActionMessage("signupCustomizeServerForm.powerOption.required"));
			}
			if(cpuOption==-1) errors.add("cpuOption", new ActionMessage("signupCustomizeServerForm.cpuOption.required"));
			if(ramOption==-1) errors.add("ramOption", new ActionMessage("signupCustomizeServerForm.ramOption.required"));
			if(sataControllerOption==-1 && limits!=null) {
				// Only required when there is at least one power option available
				boolean found = false;
				for(PackageDefinitionLimit limit : limits) {
					if(limit.getResource().getName().startsWith("hardware_disk_controller_sata_")) {
						found=true;
						break;
					}
				}
				if(found) errors.add("sataControllerOption", new ActionMessage("signupCustomizeServerForm.sataControllerOption.required"));
			}
			if(scsiControllerOption==-1 && limits!=null) {
				// Only required when there is at least one power option available
				boolean found = false;
				for(PackageDefinitionLimit limit : limits) {
					if(limit.getResource().getName().startsWith("hardware_disk_controller_scsi_")) {
						found=true;
						break;
					}
				}
				if(found) errors.add("scsiControllerOption", new ActionMessage("signupCustomizeServerForm.scsiControllerOption.required"));
			}
			// At least one hard drive must be selected
			boolean foundDisk = isAtLeastOneDiskSelected();
			if(!foundDisk) errors.add("diskOptions", new ActionMessage("signupCustomizeServerForm.atLeastOneDisk"));
			return errors;
		} catch(IOException err) {
			throw new WrappedException(err);
		} catch(SQLException err) {
			throw new WrappedException(err);
		}
	}

	public boolean isAtLeastOneDiskSelected() {
		for(String diskOption : diskOptions) {
			if(diskOption!=null && diskOption.length()>0 && !diskOption.equals("-1")) {
				return true;
			}
		}
		return false;
	}

	protected abstract String getSignupSelectPackageFormName();
}
