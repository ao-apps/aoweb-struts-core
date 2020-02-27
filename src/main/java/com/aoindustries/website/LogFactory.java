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
import com.aoindustries.aoserv.client.reseller.Category;
import com.aoindustries.aoserv.client.ticket.TicketLoggingHandler;
import com.aoindustries.util.ErrorPrinter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Provides static access to the logging facilities.  The logs are written
 * into the AOServ ticket system under the type "logs".
 *
 * @author  AO Industries, Inc.
 */
public class LogFactory {

	private LogFactory() {
	}

	private static final String INSTANCES_APPLICATION_ATTRIBUTE = LogFactory.class.getName() + ".instances";

	@WebListener
	public static class Initializer implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent event) {
			getInstances(event.getServletContext());
		}
		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Do nothing
		}
	}

	private static ConcurrentMap<String,Logger> getInstances(ServletContext servletContext) {
		@SuppressWarnings("unchecked")
		ConcurrentMap<String,Logger> instances = (ConcurrentMap<String,Logger>)servletContext.getAttribute(INSTANCES_APPLICATION_ATTRIBUTE);
		if(instances == null) {
			instances = new ConcurrentHashMap<>();
			servletContext.setAttribute(INSTANCES_APPLICATION_ATTRIBUTE, instances);
		}
		return instances;
	}

	/**
	 * Gets the logger for the provided ServletContext and class.
	 */
	public static Logger getLogger(ServletContext servletContext, Class<?> clazz) {
		return getLogger(servletContext, clazz.getName());
	}

	/**
	 * <p>
	 * Gets the logger for the provided ServletContext and name.  The logger
	 * is stored as a context attribute under the name (APPLICATION_ATTRIBUTE + '.' + name).
	 * Subsequent calls to this method will return the previously created logger.
	 * If an error occurs while creating the logger it will return the default
	 * logger.  In this case, it will not add the logger to the servletContext,
	 * which will cause it to try again until a fully functional logger is
	 * available.
	 * </p>
	 * <p>
	 * Callers of this class should request a logger each time they need one
	 * and not cache/reuse the logger provided by this method.  This allows
	 * for the automatic retry on logger creation.
	 * </p>
	 */
	public static Logger getLogger(ServletContext servletContext, String name) {
		ConcurrentMap<String,Logger> instances = getInstances(servletContext);
		Logger logger = instances.get(name);
		if(logger == null) {
			Handler handler;
			try {
				SiteSettings siteSettings = SiteSettings.getInstance(servletContext);
				AOServConnector rootConn = siteSettings.getRootAOServConnector();
				Category category = rootConn.getReseller().getCategory().getTicketCategoryByDotPath("aoserv.aoweb_struts");
				handler = TicketLoggingHandler.getHandler(
					siteSettings.getBrand().getAowebStrutsHttpUrlBase(),
					rootConn,
					category
				);
			} catch(RuntimeException | IOException | SQLException err) {
				ErrorPrinter.printStackTraces(err);
				handler = null;
			}
			logger = Logger.getLogger(name);
			if(handler != null) {
				synchronized(logger) {
					boolean foundHandler = false;
					for(Handler oldHandler : logger.getHandlers()) {
						if(oldHandler == handler) {
							foundHandler = true;
						} else {
							logger.removeHandler(oldHandler);
						}
					}
					if(!foundHandler) {
						logger.addHandler(handler);
					}
					logger.setUseParentHandlers(false);
				}
				// Only store the instance once fully associated with the correct handler
				Logger existing = instances.putIfAbsent(name, logger);
				if(existing != null) logger = existing;
			}
		}
		return logger;
	}
}
