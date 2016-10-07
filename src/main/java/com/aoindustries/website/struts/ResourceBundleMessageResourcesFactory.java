/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.struts;

import java.io.Serializable;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;

/**
 * Sets the keywords for the page.s
 *
 * @author  AO Industries, Inc.
 */
public class ResourceBundleMessageResourcesFactory extends MessageResourcesFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public MessageResources createResources(String config) {
		return new ResourceBundleMessageResources(this, config, this.returnNull);
	}
}
