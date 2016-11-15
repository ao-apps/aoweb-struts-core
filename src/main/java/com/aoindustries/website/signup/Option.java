/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016  AO Industries, Inc.
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
package com.aoindustries.website.signup;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author  AO Industries, Inc.
 */
public class Option {

	public static class PriceComparator implements Comparator<Option> {
		@Override
		public int compare(Option pdl1, Option pdl2) {
			return pdl1.getPriceDifference().compareTo(pdl2.getPriceDifference());
		}
	}

	final private int packageDefinitionLimit;
	final private String display;
	final private BigDecimal priceDifference;

	public Option(
		int packageDefinitionLimit,
		String display,
		BigDecimal priceDifference
	) {
		this.packageDefinitionLimit = packageDefinitionLimit;
		this.display = display;
		this.priceDifference = priceDifference;
	}

	public int getPackageDefinitionLimit() {
		return packageDefinitionLimit;
	}

	public String getDisplay() {
		return display;
	}

	public BigDecimal getPriceDifference() {
		return priceDifference;
	}
}
