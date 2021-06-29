/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2021  AO Industries, Inc.
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
module com.aoindustries.web.struts.core.devel {
	exports com.aoindustries.web.struts.i18n;
	exports com.aoindustries.web.struts.clientarea.i18n;
	exports com.aoindustries.web.struts.clientarea.accounting.i18n;
	exports com.aoindustries.web.struts.clientarea.control.i18n;
	exports com.aoindustries.web.struts.clientarea.ticket.i18n;
	exports com.aoindustries.web.struts.clientarea.webmail.i18n;
	exports com.aoindustries.web.struts.signup.i18n;
	// Direct
	requires com.aoapps.hodgepodge; // <groupId>com.aoapps</groupId><artifactId>ao-hodgepodge</artifactId>
}
