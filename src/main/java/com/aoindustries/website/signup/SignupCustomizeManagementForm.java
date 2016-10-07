/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.website.SessionActionForm;
import java.io.Serializable;
import org.apache.struts.action.ActionForm;

/**
 * @author  AO Industries, Inc.
 */
public class SignupCustomizeManagementForm extends ActionForm implements Serializable, SessionActionForm {

	private static final long serialVersionUID = 1L;

	private int backupOnsiteOption;
	private int backupOffsiteOption;
	private String backupDvdOption;
	private int distributionScanOption;
	private int failoverOption;
	private String formCompleted;

	public SignupCustomizeManagementForm() {
		setBackupOnsiteOption(-1);
		setBackupOffsiteOption(-1);
		setBackupDvdOption("");
		setDistributionScanOption(-1);
		setFailoverOption(-1);
		setFormCompleted("false");
	}

	@Override
	public boolean isEmpty() {
		return
			backupOnsiteOption==-1
			&& backupOffsiteOption==-1
			&& "".equals(backupDvdOption)
			&& distributionScanOption==-1
			&& failoverOption==-1
			&& "false".equals(formCompleted)
		;
	}

	public int getBackupOnsiteOption() {
		return backupOnsiteOption;
	}

	public void setBackupOnsiteOption(int backupOnsiteOption) {
		this.backupOnsiteOption = backupOnsiteOption;
	}

	public int getBackupOffsiteOption() {
		return backupOffsiteOption;
	}

	public void setBackupOffsiteOption(int backupOffsiteOption) {
		this.backupOffsiteOption = backupOffsiteOption;
	}

	public String getBackupDvdOption() {
		return backupDvdOption;
	}

	public void setBackupDvdOption(String backupDvdOption) {
		this.backupDvdOption = backupDvdOption;
	}

	public int getDistributionScanOption() {
		return distributionScanOption;
	}

	public void setDistributionScanOption(int distributionScanOption) {
		this.distributionScanOption = distributionScanOption;
	}

	public int getFailoverOption() {
		return failoverOption;
	}

	public void setFailoverOption(int failoverOption) {
		this.failoverOption = failoverOption;
	}

	public String getFormCompleted() {
		return formCompleted;
	}

	public void setFormCompleted(String formCompleted) {
		this.formCompleted = formCompleted;
	}
}
