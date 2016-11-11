/*
 * Copyright 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
// TODO: Copyright to lgplv3 recommendations in all aoweb-struts projects
package com.aoindustries.website.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
public class AowebStrutsSkinTldInitializer extends TagReferenceInitializer {

	private static final String TLD_BOOK = "/aoweb-struts/core";
	private static final String TLD_PATH = "/aoweb-struts-skin.tld";
	static final String TLD_SERVLET_PATH = TLD_BOOK + TLD_PATH;

	private static final Map<String,String> additionalApiLinks = new LinkedHashMap<String,String>();
	static {
		additionalApiLinks.put("com.aoindustries.website.skintags.", "https://aoindustries.com/aoweb-struts/core/apidocs/");
	}

	public AowebStrutsSkinTldInitializer() {
		super(
			"AOWeb Struts Skin Taglib Reference",
			"Skin Taglib Reference",
			TLD_BOOK,
			TLD_PATH,
			"https://docs.oracle.com/javase/6/docs/api/",
			"https://docs.oracle.com/javaee/6/api/",
			additionalApiLinks
		);
	}
}
