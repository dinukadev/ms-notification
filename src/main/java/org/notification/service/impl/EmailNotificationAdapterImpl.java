package org.notification.service.impl;

import java.io.UnsupportedEncodingException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.notification.constants.ProviderType;
import org.notification.domain.NotificationRequest;
import org.notification.exception.NotificationException;
import org.notification.service.NotificationAdapter;
import org.notification.utils.EmailAttachmentValidator;
import org.notification.utils.HtmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * This class will handle sending out email messages.
 * <p>
 * Note : We need the prototype scope here because we use the retry count if the invocation ever
 * failed. So each instance should have its own retry count initialized.
 *
 * @author dinuka
 */
@Component(ProviderType.EMAIL)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EmailNotificationAdapterImpl implements NotificationAdapter {

  private static Logger log = LoggerFactory.getLogger(EmailNotificationAdapterImpl.class);

  @Autowired
  private JavaMailSender mailSender;

  @Value("${emailConfig.fromAddress}")
  private String fromAddress;

  @Value("${emailConfig.fromName}")
  private String fromName;

  @Value("${emailConfig.retryCount}")
  private Integer retryCount;

  @Override
  public void sendMessage(NotificationRequest notificationRequest) {
    EmailAttachmentValidator.validateAttachmentContent(
        notificationRequest.getEmailAttachmentInfoList());
    try {
      log.info("Building the email message to be sent");
      MimeMessage message = buildMessage(notificationRequest);
      log.info("Preparing to send the email.");
      mailSender.send(message);
      log.info("Successfully sent the email message.");
    } catch (Exception e) {
      if (retryCount > 0) {
        retryCount--;
        sendMessage(notificationRequest);
      }
      log.error("Exception received while trying to send out email " +
          "message : {}", e);
      throw new NotificationException("Error occurred while trying to send " +
          "out the email.", e);
    }
  }

  private MimeMessage buildMessage(NotificationRequest notificationRequest)
      throws MessagingException, UnsupportedEncodingException {
    log.info("Building the MimeMessage to be sent");
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    // If a sender name is sent from the request, that will be used, else
    // the fromName configured in the property file will be used.
    mimeMessage.setFrom(new InternetAddress(fromAddress,
        StringUtils.isNotBlank(notificationRequest.getSender()) ?
            StringUtils.trim(notificationRequest.getSender()) : fromName));
    String content = notificationRequest.getMessage();
    if (CollectionUtils.isEmpty(notificationRequest.getEmailAttachmentInfoList())) {
      // Here we check if the content contains html or plain text and set
      // the content type appropriately.
      mimeMessage.setContent(notificationRequest.getMessage(), String.format("%s;%s",
          HtmlUtil.doesStringContainHtml(content) ?
              "text/html" : "text/plain", "charset=utf-8"));
    } else {
      Multipart multiPartMail = new MimeMultipart();
      MimeBodyPart textContent = new MimeBodyPart();
      textContent.setContent(notificationRequest.getMessage(), String.format("%s;%s",
          HtmlUtil.doesStringContainHtml(content) ? "text/html" :
              "text/plain", "charset=utf-8"));

      multiPartMail.addBodyPart(textContent);

      notificationRequest.getEmailAttachmentInfoList().stream().forEach(attInfo -> {
        MimeBodyPart attachmentBody = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(attInfo.getByteArr(),
            attInfo.getMimeType());
        try {
          attachmentBody.setDataHandler(new DataHandler(dataSource));
          attachmentBody.setFileName(attInfo.getFileName());
          multiPartMail.addBodyPart(attachmentBody);
        } catch (MessagingException e) {
          log.error("Exception occurred while setting the data handler" +
              " for the attachment : {} ", e);
          throw new NotificationException("Exception occurred while " +
              "setting the data handler for the attachment.", e);
        }
      });
      mimeMessage.setContent(multiPartMail);
    }

    if (CollectionUtils.isEmpty(notificationRequest.getToRecipients())
        && CollectionUtils.isEmpty(notificationRequest.getCcRecipients())
        && CollectionUtils.isEmpty(notificationRequest.getBccRecipients())) {
      throw new NotificationException("No recipients specified. Please " +
          "specify recipients under TO, CC or BCC");
    }
    mimeMessage.setRecipients(RecipientType.TO,
        InternetAddress.parse(StringUtils.join(
            notificationRequest.getToRecipients().iterator(), ",")));

    if (CollectionUtils.isNotEmpty(notificationRequest.getCcRecipients())) {
      mimeMessage.setRecipients(RecipientType.CC,
          InternetAddress.parse(StringUtils.join(
              notificationRequest.getCcRecipients().iterator(), ",")));
    }
    if (CollectionUtils.isNotEmpty(notificationRequest.getBccRecipients())) {
      mimeMessage.setRecipients(RecipientType.BCC,
          InternetAddress.parse(StringUtils.join(
              notificationRequest.getBccRecipients().iterator(), ",")));
    }

    //If the subject is not sent, then a subject will be generated.
    String subject = notificationRequest.getSubject();
    if (StringUtils.isNotBlank(notificationRequest.getClientId()) && "ms_messages"
        .equalsIgnoreCase(notificationRequest.getClientId())) {

      if (StringUtils.isEmpty(subject)) {
        subject = "Ref : " + notificationRequest.getUniqueReference();
      } else {
        subject += String.format("(Ref: %s)", notificationRequest.getUniqueReference());
      }
    }
    mimeMessage.setSubject(subject);
    // This value set in this header will be returned on the reply mail on
    // the header name called "References".
    mimeMessage.addHeader("In-Reply-To", notificationRequest.getUniqueReference());
    return mimeMessage;
  }
}
