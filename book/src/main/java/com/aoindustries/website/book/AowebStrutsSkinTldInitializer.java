/*
 * Copyright 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
// TODO: Copyright to lgplv3 recommendations in all aoweb-struts projects
package com.aoindustries.website.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import java.util.Collections;

/**
 * @author  AO Industries, Inc.
 */
public class AowebStrutsSkinTldInitializer extends TagReferenceInitializer {

	public AowebStrutsSkinTldInitializer() {
		super(
			"AOWeb Struts Skin Taglib Reference",
			"Skin Taglib Reference",
			"/aoweb-struts/core",
			"/aoweb-struts-skin.tld",
			"https://docs.oracle.com/javase/6/docs/api/",
			"https://docs.oracle.com/javaee/6/api/",
			Collections.singletonMap("com.aoindustries.website.skintags.", "https://aoindustries.com/aoweb-struts/core/apidocs/")
		);
	}
}
