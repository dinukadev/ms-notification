package org.notification.utils;

import org.joda.time.DateTime;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.repository.IncomingTransactionInfoMongoRepository;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IncomingMailNotificationUtil {

  private static Logger log = LoggerFactory.getLogger(IncomingMailNotificationUtil.class);

  @Value("${notification.responseTopic: #{null}}")
  private String notificationResponseTopic;

  @Autowired
  private IncomingTransactionInfoMongoRepository incomingNotifRepo;

  @Autowired
  private NotificationTransactionInfoMongoRepository notificationTransRepo;

  public void processIncomingMailMatchFound(NotificationTransactionInfo notificationTransactionInfo,
                                            IncomingTransactionInfo incomingTransaction) {
    log.info("Updating the processed date");
    incomingTransaction.setProcessedDate(DateTime.now());
    incomingNotifRepo.save(incomingTransaction);
    log.info("Successfully updated the processed date");

    log.info("Updating the status of the notification transaction info to : {}",
        NotificationStatus.RESPONSE_RECEIVED.toString());
    notificationTransactionInfo.setStatus(NotificationStatus.RESPONSE_RECEIVED);
    notificationTransRepo.save(notificationTransactionInfo);
    log.info("Successfully updated the notificaiton transaction info to : {} ",
        NotificationStatus.RESPONSE_RECEIVED.toString());

  }
}
