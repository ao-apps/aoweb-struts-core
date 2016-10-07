/*
 * Copyright 2007-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.website;

import com.aoindustries.aoserv.client.validator.HostAddress;
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
				for(String to : tos) msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
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
