package org.notification.domain;


import java.util.LinkedList;
import java.util.List;
import org.notification.service.NotificationAdapter;

/**
 * This class will hold all the information required by implementation classes
 * of {@link NotificationAdapter}
 *
 * @author dinuka
 */
public class NotificationRequest {

    private String message;

    private String subject;

    private List<String> toRecipients = new LinkedList<>();

    private List<String> ccRecipients = new LinkedList<>();

    private List<String> bccRecipients = new LinkedList<>();

    private String uniqueReference;

    private List<EmailAttachmentInfo> emailAttachmentInfoList = new LinkedList<>();

    private String sender;

    private String toNumber;

    private String clientId;

    private NotificationRequest(String message, String subject, List<String> toRecipients, List<String> ccRecipients,

                                List<String> bccRecipients, String uniqueReference, List<EmailAttachmentInfo>
										emailAttachmentInfoList,
                                String sender, String toNumber) {

        this.message = message;
        this.subject = subject;
        this.toRecipients = toRecipients;
        this.ccRecipients = ccRecipients;
        this.bccRecipients = bccRecipients;
        this.uniqueReference = uniqueReference;
        this.emailAttachmentInfoList = emailAttachmentInfoList;
        this.sender = sender;
        this.toNumber = toNumber;

    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getToRecipients() {
        return toRecipients;
    }

    public List<String> getCcRecipients() {
        return ccRecipients;
    }

    public List<String> getBccRecipients() {
        return bccRecipients;
    }

    public String getUniqueReference() {
        return uniqueReference;
    }

    public List<EmailAttachmentInfo> getEmailAttachmentInfoList() {
        return emailAttachmentInfoList;
    }

    public String getSender() {
        return sender;
    }

    public String getToNumber() {
        return toNumber;
    }

    public String getClientId() {
        return clientId;
    }

    /**
     * This builder will be used to construct an instance of this object using
     * the details needed for sending out emails.
     *
     * @author dinuka
     */
    public static class EmailNotificationRequestBuilder {

        private String message;

        private String subject;

        private List<String> toRecipients = new LinkedList<>();

        private List<String> ccRecipients = new LinkedList<>();

        private List<String> bccRecipients = new LinkedList<>();

        private String uniqueReference;

        private List<EmailAttachmentInfo> emailAttachmentInfoList = new LinkedList<>();

        private String sender;

        private String clientId;

        public EmailNotificationRequestBuilder withMessageAndSubject(String message, String subject) {
            this.message = message;
            this.subject = subject;
            return this;
        }

        public EmailNotificationRequestBuilder withToRecipients(List<String> toRecipients) {
            this.toRecipients = toRecipients;
            return this;
        }

        public EmailNotificationRequestBuilder withCCRecipients(List<String> ccRecipients) {
            this.ccRecipients = ccRecipients;
            return this;
        }

        public EmailNotificationRequestBuilder withBCCRecipients(List<String> bccRecipients) {
            this.bccRecipients = bccRecipients;
            return this;
        }

        public EmailNotificationRequestBuilder withUniqueReference(String uniqueReference) {
            this.uniqueReference = uniqueReference;
            return this;
        }

        public EmailNotificationRequestBuilder withEmailAttachmentInfoList(
                List<EmailAttachmentInfo> emailAttachmentInfoList) {
            this.emailAttachmentInfoList = emailAttachmentInfoList;
            return this;
        }

        public EmailNotificationRequestBuilder withSender(String sender) {
            this.sender = sender;
            return this;
        }

        public EmailNotificationRequestBuilder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public NotificationRequest build() {
            return new NotificationRequest(message, subject, toRecipients, ccRecipients, bccRecipients, uniqueReference,
                    emailAttachmentInfoList, sender, null);
        }

    }

    public static class SMSNotificationRequestBuilder {
        private String message;

        private String uniqueReference;

        private String toNumber;

        public SMSNotificationRequestBuilder withRecipientNumberMessageAndUniqueReferene(String toNumber,
                                                                                         String message, String
																								 uniqueReference) {
            this.toNumber = toNumber;
            this.message = message;
            this.uniqueReference = uniqueReference;
            return this;
        }

        public NotificationRequest build() {
            return new NotificationRequest(message, null, null, null, null, uniqueReference, null, null, toNumber);
        }

    }
}
