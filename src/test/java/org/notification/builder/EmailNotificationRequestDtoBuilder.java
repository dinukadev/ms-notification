package org.notification.builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.notification.rest.dto.EmailAttachmentInfoDto;
import org.notification.rest.dto.EmailNotificationRequestDto;


public class EmailNotificationRequestDtoBuilder {

  private List<String> toRecipients;

  private List<String> ccRecipients;

  private List<String> bccRecipients;

  private String message;

  private String subject;

  private List<EmailAttachmentInfoDto> emailAttachments = new LinkedList<>();

  private String sender;

  private EmailNotificationRequestDtoBuilder() {
  }

  public static EmailNotificationRequestDtoBuilder anEmailNotificationRequestDto() {
    return new EmailNotificationRequestDtoBuilder();
  }

  public EmailNotificationRequestDtoBuilder withToRecipients(List<String> toRecipients) {
    this.toRecipients = toRecipients;
    return this;
  }

  public EmailNotificationRequestDtoBuilder withCcRecipients(List<String> ccRecipients) {
    this.ccRecipients = ccRecipients;
    return this;
  }

  public EmailNotificationRequestDtoBuilder withBccRecipients(List<String> bccRecipients) {
    this.bccRecipients = bccRecipients;
    return this;
  }

  public EmailNotificationRequestDtoBuilder withMessage(String message) {
    this.message = message;
    return this;
  }

  public EmailNotificationRequestDtoBuilder withSubject(String subject) {
    this.subject = subject;
    return this;
  }

  public EmailNotificationRequestDtoBuilder withEmailAttachments(
      List<EmailAttachmentInfoDto> emailAttachments) {
    this.emailAttachments = emailAttachments;
    return this;
  }

  public EmailNotificationRequestDtoBuilder withSender(String sender) {
    this.sender = sender;
    return this;
  }

  public EmailNotificationRequestDtoBuilder randomPopulated() {
    this.toRecipients = Arrays.asList(RandomStringUtils.randomAlphabetic(3) + "@abc.com");
    this.message = RandomStringUtils.randomAlphabetic(3);
    this.subject = RandomStringUtils.randomAlphabetic(3);
    this.sender = RandomStringUtils.randomAlphabetic(3);
    return this;
  }

  public EmailNotificationRequestDto build() {
    EmailNotificationRequestDto emailNotificationRequestDto =
        new EmailNotificationRequestDto(toRecipients, ccRecipients,
            bccRecipients, message, subject, emailAttachments, sender);
    return emailNotificationRequestDto;
  }
}
