/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import java.io.Serializable;

/**
 * @author  AO Industries, Inc.
 */
public class VirtualDedicatedSignupCustomizeServerForm extends SignupCustomizeServerForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getSignupSelectPackageFormName() {
		return "virtualDedicatedSignupSelectPackageForm";
	}
}
