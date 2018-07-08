package org.notification.service.incomingmailchain.impl;

import org.apache.commons.lang3.StringUtils;
import org.notification.constants.MSNotificationConstants.IncomingMailMatchingChainConstants;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.notification.service.incomingmailchain.EmailMatchingChain;
import org.notification.utils.IncomingMailNotificationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This class will try to match the incoming mail by the subject, status and the sender.
 *
 * @author dinuka
 */
@Service
@Qualifier(IncomingMailMatchingChainConstants.SUBJECT_MATCHING_HANDLER)
public class SubjectMatchingHandler implements EmailMatchingChain {

  private static Logger log = LoggerFactory.getLogger(EmailMatchingChain.class);

  private EmailMatchingChain next;

  @Autowired
  private NotificationTransactionInfoMongoRepository notificationTransRepo;

  @Autowired
  private IncomingMailNotificationUtil incomingNotificationUtil;

  /**
   * {@inheritDoc}
   */
  public void next(EmailMatchingChain next) {
    this.next = next;
  }

  /**
   * {@inheritDoc}
   */
  public void handle(IncomingTransactionInfo incomingTransaction) {
    log.info("Trying to find a match in SubjectMatchingHandler for subject : {} , Sender: {}",
        incomingTransaction.getMessageSubject(), incomingTransaction.getSender());
    NotificationTransactionInfo outputTransactionInfo =
        notificationTransRepo.findBySubjectStatusAndSender(
            StringUtils.trimToEmpty(incomingTransaction.getMessageSubject()),
            NotificationStatus.SENT.name(),
            StringUtils.trimToEmpty(incomingTransaction.getSender()));
    if (outputTransactionInfo != null) {
      log.info("Match found for  subject : {} , Status : {} , Sender: {}",
          incomingTransaction.getMessageSubject(), NotificationStatus.SENT.name(),
          incomingTransaction.getSender());
      incomingNotificationUtil
          .processIncomingMailMatchFound(outputTransactionInfo, incomingTransaction);

    }
    if (next != null) {
      next.handle(incomingTransaction);
    }
  }

}
