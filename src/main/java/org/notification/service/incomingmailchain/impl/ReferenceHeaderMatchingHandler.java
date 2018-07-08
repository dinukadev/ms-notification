package org.notification.service.incomingmailchain.impl;

import javax.mail.Header;
import org.apache.commons.lang3.StringUtils;
import org.notification.constants.MSNotificationConstants.IncomingMailMatchingChainConstants;
import org.notification.domain.IncomingTransactionInfo;
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
 * This class will try to match the incoming mail by the reference number embedded in the
 * "References" email header.
 *
 * @author dinuka
 */
@Service
@Qualifier(IncomingMailMatchingChainConstants.REFERENCE_HEADER_MATCHING_HANDLER)
public class ReferenceHeaderMatchingHandler implements EmailMatchingChain {

  private static Logger log = LoggerFactory.getLogger(ReferenceHeaderMatchingHandler.class);

  private EmailMatchingChain next;

  @Autowired
  private NotificationTransactionInfoMongoRepository transactionInfoRepo;

  @Autowired
  private IncomingMailNotificationUtil incomingMailUtil;

  /**
   * {@inheritDoc}
   */
  public void next(EmailMatchingChain next) {
    this.next = next;

  }

  /**
   * {@inheritDoc}
   */
  public void handle(IncomingTransactionInfo transactionInfo) {
    log.info(
        "Trying to find a match in ReferenceHeaderMatchingHandler for subject : {} , Sender: {}",
        transactionInfo.getMessageSubject(), transactionInfo.getSender());
    boolean matchFound = false;
    if (transactionInfo.getEmailHeaders() != null) {
      outer:
      for (Header referenceHeader : transactionInfo.getEmailHeaders()) {
        if ("References".equalsIgnoreCase(referenceHeader.getName())) {
          String referenceHeaderValue = referenceHeader.getValue();
          // The regex here will work for both windows and linux as in
          // linux you only have the line feed where as in windows you
          // have the carriage return and line feed.
          String[] refHeaderValueArr = referenceHeaderValue.split("\r\n|\n");
          for (String refHeaderValue : refHeaderValueArr) {
            String refValue =
                StringUtils.trimToEmpty(refHeaderValue.replace("<", "").replace(">", ""));
            NotificationTransactionInfo notif = transactionInfoRepo.findByReferenceNumber(refValue);

            if (notif != null) {
              log.info("Match found on the reference header for reference value : {}", refValue);
              incomingMailUtil.processIncomingMailMatchFound(notif, transactionInfo);
              matchFound = true;
              break outer;
            }
          }

        }
      }
    }

    if (next != null && !matchFound) {
      next.handle(transactionInfo);
    }
  }

}
