package org.notification.service.impl;

import java.util.List;
import org.joda.time.DateTime;
import org.notification.constants.MSNotificationConstants;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.repository.IncomingTransactionInfoMongoRepository;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.notification.service.IncomingTransactionHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This class will handle the incoming sms messages and correlate them with the messages sent.
 *
 * @author dinuka
 */
@Service
@Qualifier(MSNotificationConstants.INCOMING_SMS_PROCESSOR_IMPL_QUALIFIER)
public class IncomingSMSTransactionHandlerServiceImpl implements IncomingTransactionHandlerService {

  private static Logger log = LoggerFactory.getLogger(SMSGatewayPollerServiceImpl.class);

  @Autowired
  private NotificationTransactionInfoMongoRepository notificationTransactionInfoMongoRepository;

  @Autowired
  private IncomingTransactionInfoMongoRepository incomingTransactionInfoMongoRepository;


  @Value("${notification.responseTopic: #{null}}")
  private String notificationResponseTopic;

  @Async
  public void process(List<IncomingTransactionInfo> incomingTransactionsInfo) {
    try {
      for (IncomingTransactionInfo incomingTransInfo : incomingTransactionsInfo) {
        NotificationTransactionInfo notifTransInfo =
            notificationTransactionInfoMongoRepository
                .findByReferenceNumberAnRecipientNumber(
                    incomingTransInfo.getReferenceNumber(),
                    incomingTransInfo.getSender());
        if (notifTransInfo != null) {
          incomingTransInfo.setProcessedDate(DateTime.now());
          incomingTransactionInfoMongoRepository.save(incomingTransInfo);

          notifTransInfo.setStatus(NotificationStatus.RESPONSE_RECEIVED);
          notificationTransactionInfoMongoRepository.save(notifTransInfo);
          log.info("Successfully updated IncomingTransactionInfo and " +
                  "notification transaction info to : {} ",
              NotificationStatus.RESPONSE_RECEIVED.toString());
        }
      }
    } catch (Exception e) {
      String exceptionStr = String.format("Exception trying to process incoming transactions.");
      log.error(exceptionStr, e);
      throw new RuntimeException(exceptionStr);
    }
  }
}
