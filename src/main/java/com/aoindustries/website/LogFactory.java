/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.TicketCategory;
import com.aoindustries.aoserv.client.TicketLoggingHandler;
import com.aoindustries.util.ErrorPrinter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * Provides static access to the logging facilities.  The logs are written
 * into the AOServ ticket system under the type "logs".
 *
 * @author  AO Industries, Inc.
 */
public class LogFactory {

	private LogFactory() {
	}

	public static final String ATTRIBUTE_NAME = LogFactory.class.getName();

	/**
	 * Gets the logger for the provided ServletContext and class.
	 */
	public static Logger getLogger(ServletContext servletContext, Class<?> clazz) {
		return getLogger(servletContext, clazz.getName());
	}

	/**
	 * <p>
	 * Gets the logger for the provided ServletContext and name.  The logger
	 * is stored as a context attribute under the name (ATTRIBUTE_NAME+'.'+name).
	 * Subsequent calls to this method will return the previously created logger.
	 * If an error occurs while creating the logger it will return the default
	 * logger.  In this case, it will not add the logger to the servletContext,
	 * which will cause it to try again until a fully functional logger is
	 * available.
	 * </p>
	 * <p>
	 * Callers of this class should reqest a logger each time they need one
	 * and not cache/reuse the logger provided by this method.  This allows
	 * for the automatic retry on logger creation.
	 * </p>
	 */
	synchronized public static Logger getLogger(ServletContext servletContext, String name) {
		String contextKey = ATTRIBUTE_NAME + '.' + name;
		Logger logger = (Logger)servletContext.getAttribute(contextKey);
		if(logger==null) {
			Handler handler;
			try {
				SiteSettings siteSettings = SiteSettings.getInstance(servletContext);
				AOServConnector rootConn = siteSettings.getRootAOServConnector();
				TicketCategory category = rootConn.getTicketCategories().getTicketCategoryByDotPath("aoserv.aoweb_struts");
				handler = TicketLoggingHandler.getHandler(
					siteSettings.getBrand().getAowebStrutsHttpUrlBase(),
					rootConn,
					category
				);
			} catch(Exception err) {
				ErrorPrinter.printStackTraces(err);
				handler = null;
			}
			logger = Logger.getLogger(name);
			if(handler!=null) {
				synchronized(logger) {
					boolean foundHandler = false;
					for(Handler oldHandler : logger.getHandlers()) {
						if(oldHandler==handler) foundHandler = true;
						else logger.removeHandler(oldHandler);
					}
					if(!foundHandler) logger.addHandler(handler);
					logger.setUseParentHandlers(false);
				}
				servletContext.setAttribute(contextKey, logger);
			}
		}
		return logger;
	}
}
