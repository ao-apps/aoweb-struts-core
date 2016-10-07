/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.signup;

import com.aoindustries.aoserv.client.PackageCategory;
import java.io.Serializable;

/**
 * @author  AO Industries, Inc.
 */
public class ColocationSignupSelectPackageForm extends SignupSelectPackageForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getPackageCategory() {
		return PackageCategory.COLOCATION;
	}
}
