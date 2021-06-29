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
module com.aoindustries.web.struts.core {
	exports com.aoindustries.web.struts;
	exports com.aoindustries.web.struts.aowebtags;
	exports com.aoindustries.web.struts.clientarea;
	exports com.aoindustries.web.struts.clientarea.accounting;
	exports com.aoindustries.web.struts.clientarea.control.account;
	exports com.aoindustries.web.struts.clientarea.control.monitor;
	exports com.aoindustries.web.struts.clientarea.control.password;
	exports com.aoindustries.web.struts.clientarea.control.vnc;
	exports com.aoindustries.web.struts.clientarea.ticket;
	exports com.aoindustries.web.struts.signup;
	exports com.aoindustries.web.struts.skintags;
	exports com.aoindustries.web.struts.struts;
	// ApplicationResources
	opens com.aoindustries.web.struts.i18n;
	opens com.aoindustries.web.struts.clientarea.i18n;
	opens com.aoindustries.web.struts.clientarea.accounting.i18n;
	opens com.aoindustries.web.struts.clientarea.control.i18n;
	opens com.aoindustries.web.struts.clientarea.ticket.i18n;
	opens com.aoindustries.web.struts.clientarea.webmail.i18n;
	opens com.aoindustries.web.struts.signup.i18n;
	// Direct
	requires com.aoapps.collections; // <groupId>com.aoapps</groupId><artifactId>ao-collections</artifactId>
	requires com.aoapps.encoding; // <groupId>com.aoapps</groupId><artifactId>ao-encoding</artifactId>
	requires com.aoapps.encoding.servlet; // <groupId>com.aoapps</groupId><artifactId>ao-encoding-servlet</artifactId>
	requires com.aoapps.encoding.taglib; // <groupId>com.aoapps</groupId><artifactId>ao-encoding-taglib</artifactId>
	requires com.aoapps.html; // <groupId>com.aoapps</groupId><artifactId>ao-fluent-html</artifactId>
	requires com.aoapps.html.any; // <groupId>com.aoapps</groupId><artifactId>ao-fluent-html-any</artifactId>
	requires com.aoapps.html.servlet; // <groupId>com.aoapps</groupId><artifactId>ao-fluent-html-servlet</artifactId>
	requires com.aoapps.html.util; // <groupId>com.aoapps</groupId><artifactId>ao-fluent-html-util</artifactId>
	requires com.aoapps.hodgepodge; // <groupId>com.aoapps</groupId><artifactId>ao-hodgepodge</artifactId>
	requires com.aoapps.io.buffer; // <groupId>com.aoapps</groupId><artifactId>ao-io-buffer</artifactId>
	requires com.aoapps.lang; // <groupId>com.aoapps</groupId><artifactId>ao-lang</artifactId>
	requires com.aoapps.net.types; // <groupId>com.aoapps</groupId><artifactId>ao-net-types</artifactId>
	requires com.aoapps.payments.api; // <groupId>com.aoapps</groupId><artifactId>ao-payments-api</artifactId>
	requires com.aoapps.servlet.filter; // <groupId>com.aoapps</groupId><artifactId>ao-servlet-filter</artifactId>
	requires com.aoapps.servlet.lastmodified; // <groupId>com.aoapps</groupId><artifactId>ao-servlet-last-modified</artifactId>
	requires com.aoapps.servlet.util; // <groupId>com.aoapps</groupId><artifactId>ao-servlet-util</artifactId>
	requires com.aoapps.sql; // <groupId>com.aoapps</groupId><artifactId>ao-sql</artifactId>
	requires com.aoapps.style; // <groupId>com.aoapps</groupId><artifactId>ao-style</artifactId>
	requires com.aoapps.taglib; // <groupId>com.aoapps</groupId><artifactId>ao-taglib</artifactId>
	requires com.aoapps.tempfiles.servlet; // <groupId>com.aoapps</groupId><artifactId>ao-tempfiles-servlet</artifactId>
	requires com.aoapps.web.resources.registry; // <groupId>com.aoapps</groupId><artifactId>ao-web-resources-registry</artifactId>
	requires com.aoapps.web.resources.renderer; // <groupId>com.aoapps</groupId><artifactId>ao-web-resources-renderer</artifactId>
	requires com.aoapps.web.resources.servlet; // <groupId>com.aoapps</groupId><artifactId>ao-web-resources-servlet</artifactId>
	requires com.aoindustries.aoserv.client; // <groupId>com.aoindustries</groupId><artifactId>aoserv-client</artifactId>
	requires com.aoindustries.aoserv.payments; // <groupId>com.aoindustries</groupId><artifactId>aoserv-credit-cards</artifactId>
	requires com.aoindustries.aoserv.daemon.client; // <groupId>com.aoindustries</groupId><artifactId>aoserv-daemon-client</artifactId>
	requires commons.validator; // <groupId>commons-validator</groupId><artifactId>commons-validator</artifactId>
	requires java.mail; // <groupId>com.sun.mail</groupId><artifactId>javax.mail</artifactId>
	requires javax.servlet.api; // <groupId>javax.servlet</groupId><artifactId>javax.servlet-api</artifactId>
	requires javax.servlet.jsp.api; // <groupId>javax.servlet.jsp</groupId><artifactId>javax.servlet.jsp-api</artifactId>
	requires javax.websocket.api; // <groupId>javax.websocket</groupId><artifactId>javax.websocket-api</artifactId>
	requires struts.core; // <groupId>org.apache.struts</groupId><artifactId>struts-core</artifactId>
	requires taglibs.standard.spec; // <groupId>org.apache.taglibs</groupId><artifactId>taglibs-standard-spec</artifactId>
	// Java SE
	requires java.logging;
	requires java.sql;
}
