package org.notification.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * This collection will store all the transcation information pertaining to the
 * notifications sent out from ms-notification.
 *
 * @author dinuka
 *
 */
@Document(collection = "notificationTransactionInfo")
public class NotificationTransactionInfo {

	@Id
	private String id;

	private String clientId;

	private String referenceNumber;

	// this indicates whether it is an email, sms etc
	private MessageType messageType;

	// indicates the status as an enum. Values would be SENT, FAILED, QUEUED
    private NotificationStatus status;

    private String message;

    private DateTime receivedDate;

    private DateTime processedDate;

    private String sender;

    private EmailInfo emailInfo;

    private SMSInfo smsInfo;

	public NotificationTransactionInfo(String clientId, String referenceNumber,
			MessageType messagType, NotificationStatus status, String message,
			DateTime receivedDate, DateTime processedDate, String sender) {
		this.clientId = clientId;
		this.referenceNumber = referenceNumber;
		this.messageType = messagType;
		this.status = status;
		this.message = message;
		this.receivedDate = receivedDate;
		this.processedDate = processedDate;
		this.sender = sender;
	}

	public NotificationTransactionInfo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public MessageType getMessagType() {
		return messageType;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public EmailInfo getEmailInfo() {
		return emailInfo;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}

	public DateTime getReceivedDate() {
		return receivedDate;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setReceivedDate(DateTime receivedDate) {
		this.receivedDate = receivedDate;
	}

	public void setProcessedDate(DateTime processedDate) {
		this.processedDate = processedDate;
	}

	public void setEmailInfo(EmailInfo emailInfo) {
		this.emailInfo = emailInfo;
	}

	public DateTime getProcessedDate() {
		return processedDate;
	}

	public String getSender() {
		return sender;
	}

	public SMSInfo getSmsInfo() {
		return smsInfo;
	}

	public void setSmsInfo(SMSInfo smsInfo) {
		this.smsInfo = smsInfo;
	}
}
