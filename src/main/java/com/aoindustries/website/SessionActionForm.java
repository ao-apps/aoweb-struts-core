/*
 * Copyright 2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

/**
 * It order to minimize the chance of jsessionid being sent to search engines,
 * any session form must provide when it is empty.  If all session forms are
 * empty, then the jsessionid will not be sent.
 *
 * @author Dan Armstrong &lt;dan@aoindustries.com&gt;
 */
public interface SessionActionForm {

	/**
	 * Indicates this form is empty, meaning that a default instance may be used
	 * in its place with no consequences.
	 */
	boolean isEmpty();
}
