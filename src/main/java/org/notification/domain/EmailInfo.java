package org.notification.domain;

import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;

import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode
public class EmailInfo {

    private List<String> toRecipients;

    private List<String> ccRecipients;

    private List<String> bccRecipients;

    private String subject;

    @Transient
    private List<EmailAttachmentInfo> emailAttachmentsInfo = new LinkedList<>();

    public EmailInfo(List<String> toRecipients, List<String> ccRecipients,
                     List<String> bccRecipients, String subject,
                     List<EmailAttachmentInfo> emailAttachmentsInfo) {
        this.toRecipients = toRecipients;
        this.ccRecipients = ccRecipients;
        this.bccRecipients = bccRecipients;
        this.subject = subject;
        this.emailAttachmentsInfo = emailAttachmentsInfo;
    }

    public EmailInfo() {
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

    public String getSubject() {
        return subject;
    }

    public List<EmailAttachmentInfo> getEmailAttachmentsInfo() {
        return emailAttachmentsInfo;
    }
}
