/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.website;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.ticket.TicketLoggingHandler;
import com.aoindustries.util.ErrorPrinter;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Logs to tickets using {@link SiteSettings#getRootAOServConnector()}
 * and {@link TicketLoggingHandler}.
 * <p>
 * Only adds itself if there are no loggers with any handler that is a
 * {@link TicketLoggingHandler}.  Thus, any <code>logging.properties</code>
 * configuration will take priority.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
@WebListener
public class TicketLoggingInitializer implements ServletContextListener {

	private static final boolean DEBUG = false;

	private volatile Thread thread;

	private volatile TicketLoggingHandler handler;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		SiteSettings siteSettings = SiteSettings.getInstance(servletContext);
		if(DEBUG) servletContext.log("Got SiteSettings: " + siteSettings.getClass().getName());
		LogManager logManager = LogManager.getLogManager();

		// Re-runnable code that will install.  Only returns on success.
		Callable<Void> installer = () -> {
			// If there is not already an instance if ticket logging registered on root handler, add one
			// Search all handlers of all loggers
			boolean found = false;
			Enumeration<String> names = logManager.getLoggerNames();
			while(names.hasMoreElements()) {
				String name = names.nextElement();
				if(DEBUG) servletContext.log("Getting logger: " + name);
				Logger l = logManager.getLogger(name);
				if(l != null) {
					for(Handler h : l.getHandlers()) {
						if(h != null) {
							if(DEBUG) servletContext.log("Checking handler: " + h.getClass().getName());
							if(h instanceof TicketLoggingHandler) {
								found = true;
								if(DEBUG) servletContext.log("Found handler!!!: " + h.getClass().getName());
								break;
							}
						}
					}
				}
			}
			if(!found) {
				if(DEBUG) servletContext.log("Handler not found, adding");
				AOServConnector rootConn = siteSettings.getRootAOServConnector();
				try {
					logManager.getLogger("").addHandler(
						handler = TicketLoggingHandler.getHandler(
							siteSettings.getBrand().getAowebStrutsHttpUrlBase(),
							rootConn,
							"aoserv.aoweb_struts"
						)
					);
					if(DEBUG) servletContext.log("Successfully added");
				} catch(SecurityException e) {
					if(DEBUG) servletContext.log("SecurityException, not added");
					// OK
				}
			}
			return null;
		};
		// Try once on current thread
		try {
			installer.call();
		} catch(Exception e) {
			ErrorPrinter.printStackTraces(e, System.err);
			// Try more in a background thread
			if(DEBUG) servletContext.log("Starting installer thread");
			(thread = new Thread(
				() -> {
					Thread currentThread = Thread.currentThread();
					while(thread == currentThread) {
						try {
							if(DEBUG) servletContext.log("Sleeping 10 seconds in Thread to try again");
							Thread.sleep(10000);
							installer.call();
							thread = null;
						} catch(Exception e2) {
							if(thread == currentThread) {
								ErrorPrinter.printStackTraces(e2, System.err);
							}
						}
					}
				},
				"Adding " + TicketLoggingHandler.class.getName()
			)).start();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		// Stop thread
		Thread t = thread;
		thread = null;
		if(t != null) {
			if(DEBUG) servletContext.log("Stopping installer thread");
			t.interrupt();
		}
		// Remove handler if we added it
		TicketLoggingHandler h = handler;
		handler = null;
		if(h != null) {
			if(DEBUG) servletContext.log("Removing handler");
			try {
				LogManager.getLogManager().getLogger("").removeHandler(h);
				if(DEBUG) servletContext.log("Successfully removed");
			} catch(SecurityException e) {
				if(DEBUG) servletContext.log("SecurityException, not removed");
				// OK
			}
		} else {
			if(DEBUG) servletContext.log("Handler not set - nothing to remove");
		}
	}
}
