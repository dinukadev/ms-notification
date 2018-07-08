package org.notification.domain;

import java.util.List;

import javax.mail.Header;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author dinuka
 *
 */
@Document(collection = "incomingTransactionInfo")
public class IncomingTransactionInfo {

	@Id
	private String id;

	private String messageBody;

	private String messageSubject;

	@Indexed
	private String sender;

	private MessageType messageType;

	private DateTime receivedDate;

	private DateTime processedDate;

	// We do not want to persist the headers
	@Transient
	private List<Header> emailHeaders;

	private String referenceNumber;

	public IncomingTransactionInfo() {
	}

	public IncomingTransactionInfo(
			String messageBody, String messageSubject, String sender,
			MessageType messageType, DateTime receivedDate, List<Header> emailHeaders) {
		this.messageBody = messageBody;
		this.messageSubject = messageSubject;
		this.sender = sender;
		this.messageType = messageType;
		this.receivedDate = receivedDate;
		this.emailHeaders = emailHeaders;
	}

	public IncomingTransactionInfo(String messageBody, String sender, MessageType messageType,
			DateTime receivedDate, String referenceNumber) {
		this.messageBody = messageBody;
		this.sender = sender;
		this.messageType = messageType;
		this.receivedDate = receivedDate;
		this.referenceNumber = referenceNumber;
	}

	public String getId() {
		return id;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public String getMessageSubject() {
		return messageSubject;
	}

	public String getSender() {
		return sender;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public DateTime getReceivedDate() {
		return receivedDate;
	}

	public void setProcessedDate(DateTime processedDate) {
		this.processedDate = processedDate;
	}

	public DateTime getProcessedDate() {
		return processedDate;
	}

	public List<Header> getEmailHeaders() {
		return emailHeaders;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

}
