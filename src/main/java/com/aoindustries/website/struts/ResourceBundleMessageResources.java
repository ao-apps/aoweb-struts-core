/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2009, 2016  AO Industries, Inc.
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
package com.aoindustries.website.struts;

import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.struts.util.MessageResources;

/**
 * Sets the keywords for the page.
 *
 * @author  AO Industries, Inc.
 */
public class ResourceBundleMessageResources extends MessageResources implements Serializable {

	private static final long serialVersionUID = 1L;

	private static volatile boolean cachedEnabled = true;

	public static void setCachedEnabled(boolean cachedEnabled) {
		ResourceBundleMessageResources.cachedEnabled = cachedEnabled;
	}

	public ResourceBundleMessageResources(ResourceBundleMessageResourcesFactory factory, String config) {
		this(factory, config, false);
	}

	public ResourceBundleMessageResources(ResourceBundleMessageResourcesFactory factory, String config, boolean returnNull) {
		super(factory, config, returnNull);
	}

	@Override
	public String getMessage(Locale locale, String key) {
		String value = null;
		try {
			ResourceBundle applicationResources = ResourceBundle.getBundle(config, locale);
			value = applicationResources.getString(key);
		} catch(MissingResourceException err) {
			// string remains null
		}

		if(value!=null) return value;
		if(returnNull) return null;
		return "???"+locale.toString()+"."+key+"???";
	}

	@Override
	public String getMessage(Locale locale, String key, Object args[]) {
		String message = super.getMessage(locale, key, args);
		if(!cachedEnabled) {
			synchronized(formats) {
				formats.clear();
			}
		}
		return message;
	}
}
