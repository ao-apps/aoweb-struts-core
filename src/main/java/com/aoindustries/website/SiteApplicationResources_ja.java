/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.util.i18n.EditableResourceBundle;
import java.io.File;
import java.util.Locale;

/**
 * Provides a simplified interface for obtaining localized values from the ApplicationResources.properties files.
 * Is also an editable resource bundle.
 *
 * @author  AO Industries, Inc.
 */
public final class SiteApplicationResources_ja extends EditableResourceBundle {

	/**
	 * Do not use directly.
	 */
	public SiteApplicationResources_ja() {
		super(
			Locale.JAPANESE,
			SiteApplicationResources.bundleSet,
			new File(System.getProperty("user.home")+"/common/ao/cvswork/aoweb-struts/WEB-INF/classes/com/aoindustries/website/SiteApplicationResources_ja.properties")
		);
	}
}
