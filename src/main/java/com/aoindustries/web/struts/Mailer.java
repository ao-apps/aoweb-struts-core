/*
 * aoweb-struts-core - Core API for legacy Struts-based site framework with AOServ Platform control panels.
 * Copyright (C) 2007-2009, 2016, 2017, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.web.struts;

import com.aoapps.net.HostAddress;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Centralized mailer.  Because it must set system properties for correct behavior (thanks javamail!) this
 * will only send one email at a time.
 *
 * @author  AO Industries, Inc.
 */
final public class Mailer {

	private static final Object mailerLock = new Object();

	/**
	 * Make no instances.
	 */
	private Mailer() {}

	/**
	 * Sends an email.
	 */
	public static void sendEmail(
		HostAddress smtpServer,
		String contentType,
		String charset,
		String fromAddress,
		String fromPersonal,
		List<String> tos,
		String subject,
		String message
	) throws MessagingException, UnsupportedEncodingException {
		synchronized(mailerLock) {
			System.setProperty("mail.mime.charset", charset);
			try {
				// Create the email
				Properties props=new Properties();
				props.put("mail.smtp.host", smtpServer);
				Session mailSession=Session.getDefaultInstance(props, null);
				Message msg=new MimeMessage(mailSession);
				msg.setFrom(
					new InternetAddress(
						fromAddress,
						fromPersonal
					)
				);
				for(String to : tos) {
					msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, true));
				}
				msg.setSubject(subject);
				msg.setSentDate(new Date(System.currentTimeMillis()));

				ContentType ct = new ContentType(contentType);
				ct.setParameter("charset", charset);
				msg.setContent(message, ct.toString());
				Transport.send(msg);
			} finally {
				System.clearProperty("mail.mime.charset");
			}
		}
	}
}
