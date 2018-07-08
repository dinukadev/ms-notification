package org.notification.service.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import org.notification.constants.MSNotificationConstants;
import org.notification.constants.MSNotificationConstants.IncomingMailMatchingChainConstants;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.NotificationStatus;
import org.notification.service.IncomingTransactionHandlerService;
import org.notification.service.incomingmailchain.EmailMatchingChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author dinuka
 */
@Service
@Qualifier(MSNotificationConstants.INCOMING_MAIL_PROCESSOR_IMPL_QUALIFIER)
public class IncomingMailTransactionHandlerServiceImpl implements
    IncomingTransactionHandlerService {

  private static Logger log =
      LoggerFactory.getLogger(IncomingMailTransactionHandlerServiceImpl.class);

  @Autowired
  @Qualifier(IncomingMailMatchingChainConstants.REFERENCE_HEADER_MATCHING_HANDLER)
  private EmailMatchingChain referenceHeaderMatchingHandler;

  @Autowired
  @Qualifier(IncomingMailMatchingChainConstants.SUBJECT_MATCHING_HANDLER)
  private EmailMatchingChain subjectMatchingHandler;

  @PostConstruct
  public void init() {
    // Build up the handler chain. The order is as follows;
    // 1. Reference header matcher
    // 2. Subject matcher
    referenceHeaderMatchingHandler.next(subjectMatchingHandler);
  }

  @Async
  @Override
  public void process(List<IncomingTransactionInfo> mailList) {
    log.info("Processing the emails read off from the server");

    for (IncomingTransactionInfo incomingTransaction : mailList) {
      log.info("Trying to find a match for subject : {} , Status : {} , Sender: {}",
          incomingTransaction.getMessageSubject(), NotificationStatus.SENT.name(),
          incomingTransaction.getSender());
      referenceHeaderMatchingHandler.handle(incomingTransaction);
    }
  }

}