/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website.skintags;

import java.util.Collection;

/**
 * Something with a set of meta data.
 *
 * @author  AO Industries, Inc.
 */
public interface MetasAttribute {

	Collection<Meta> getMetas();

	void addMeta(Meta meta);
}
