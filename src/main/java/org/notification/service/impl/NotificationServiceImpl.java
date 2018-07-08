package org.notification.service.impl;

import org.joda.time.DateTime;
import org.notification.Application;
import org.notification.constants.ProviderType;
import org.notification.domain.NotificationRequest;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.exception.InvalidEmailAttachmentException;
import org.notification.exception.NotificationException;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.notification.rest.dto.NotificationResponseDto;
import org.notification.service.NotificationAdapterFactory;
import org.notification.service.NotificationService;
import org.notification.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author dinuka
 */
@Service
public class NotificationServiceImpl implements NotificationService {

  private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

  @Autowired
  private NotificationTransactionInfoMongoRepository notificationTransactionRepo;

  @Autowired
  private NotificationAdapterFactory notificationAdapterFactory;


  @Autowired
  private CommonUtil commonUtil;

  @Value("${emailConfig.active}")
  private boolean isEmailActive;

  @Value("${smsConfig.active}")
  private boolean isSmsActive;

  @Override
  public NotificationResponseDto sendEmailSync(
      NotificationTransactionInfo notificationTransactionInfo) {

    String status = NotificationStatus.CREATED.toString();
    String clientId = notificationTransactionInfo.getClientId();
    NotificationResponseDto responseDto = new NotificationResponseDto(
        notificationTransactionInfo.getReferenceNumber(), status, clientId);

    log.info("Persisting the notification transaction");
    NotificationTransactionInfo notifTransInfo = notificationTransactionRepo
        .insert(notificationTransactionInfo);

    NotificationRequest notifRequest = new NotificationRequest.EmailNotificationRequestBuilder()
        .withToRecipients(notificationTransactionInfo.getEmailInfo().getToRecipients())
        .withCCRecipients(notificationTransactionInfo.getEmailInfo().getCcRecipients())
        .withBCCRecipients(notificationTransactionInfo.getEmailInfo().getBccRecipients())
        .withMessageAndSubject(notificationTransactionInfo.getMessage(),
            notificationTransactionInfo.getEmailInfo().getSubject())
        .withUniqueReference(notificationTransactionInfo.getReferenceNumber())
        .withEmailAttachmentInfoList(notificationTransactionInfo.getEmailInfo()
            .getEmailAttachmentsInfo())
        .withSender(notificationTransactionInfo.getSender())
        .withClientId(notificationTransactionInfo.getClientId()).build();

    try {
      log.info("Calling the Email notification adapter");
      if (isEmailActive) {
        notificationAdapterFactory.getAdapter(ProviderType.EMAIL).sendMessage(notifRequest);
      }
      notifTransInfo.setStatus(NotificationStatus.SENT);
      log.info("Successfully called the Email notification adapter");
    } catch (NotificationException | InvalidEmailAttachmentException e) {
      log.error("Exception occurred : {}", e);
      notifTransInfo.setStatus(NotificationStatus.FAILED);
      throw e;
    } finally {
      notifTransInfo.setProcessedDate(DateTime.now());
      // Update the record with the status.
      notificationTransactionRepo.save(notificationTransactionInfo);
      responseDto.setStatus(notificationTransactionInfo.getStatus().toString());
    }

    return responseDto;
  }

  /**
   * This method will be called asynchronously by spring using a background thread so the caller
   * will not be blocked. See {@link Application#getAsyncExecutor()} where a cached thread pool
   * executor is configured.
   */
  @Async
  public void sendEmailAsync(NotificationTransactionInfo notificationTransactionInfo) {
    sendEmailSync(notificationTransactionInfo);
  }

  @Override
  public NotificationResponseDto sendSmsSync(
      NotificationTransactionInfo notificationTransactionInfo) {
    String status = NotificationStatus.CREATED.toString();
    String clientId = notificationTransactionInfo.getClientId();
    NotificationResponseDto responseDto = new NotificationResponseDto(
        notificationTransactionInfo.getReferenceNumber(), status, clientId);
    log.info("Persisting the notification transaction");
    NotificationTransactionInfo notifTransInfo = notificationTransactionRepo
        .insert(notificationTransactionInfo);

    NotificationRequest notificationRequest =
        new NotificationRequest.SMSNotificationRequestBuilder()
            .withRecipientNumberMessageAndUniqueReferene(
                notificationTransactionInfo.getSmsInfo().getToNumber(),
                notificationTransactionInfo.getMessage(),
                notificationTransactionInfo.getReferenceNumber())
            .build();
    try {
      log.info("Calling the SMS notification adapter");
      if (isSmsActive) {
        notificationAdapterFactory.getAdapter(ProviderType.SMS).sendMessage(
            notificationRequest);
      }
      notifTransInfo.setStatus(NotificationStatus.SENT);
      log.info("Successfully called the SMS notification adapter");
    } catch (NotificationException | InvalidEmailAttachmentException e) {
      log.error("Exception occurred : {}", e);
      notifTransInfo.setStatus(NotificationStatus.FAILED);
      throw e;
    } finally {
      notifTransInfo.setProcessedDate(DateTime.now());
      notificationTransactionRepo.save(notificationTransactionInfo);
      responseDto.setStatus(notificationTransactionInfo.getStatus().toString());
    }
    return responseDto;
  }

  @Async
  public void sendSMSAsync(final NotificationTransactionInfo notificationTransactionInfo) {
    sendSmsSync(notificationTransactionInfo);
  }
}
