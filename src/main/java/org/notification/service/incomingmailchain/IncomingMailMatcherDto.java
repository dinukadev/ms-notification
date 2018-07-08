package org.notification.service.incomingmailchain;

import java.util.Enumeration;

import javax.mail.Header;

/**
 * 
 * @author dinuka
 *
 */
public class IncomingMailMatcherDto {

	private String sender;

	private String subject;

	private Enumeration<Header> mailHeaders;

	public IncomingMailMatcherDto(String sender, String subject, Enumeration<Header> mailHeaders) {
		this.sender = sender;
		this.subject = subject;
		this.mailHeaders = mailHeaders;
	}

	public String getSender() {
		return sender;
	}

	public String getSubject() {
		return subject;
	}

	public Enumeration<Header> getMailHeaders() {
		return mailHeaders;
	}

}
