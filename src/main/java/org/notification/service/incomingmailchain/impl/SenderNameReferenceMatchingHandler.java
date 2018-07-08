package org.notification.service.incomingmailchain.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Header;
import org.apache.commons.lang3.StringUtils;
import org.notification.constants.MSNotificationConstants.IncomingMailMatchingChainConstants;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.notification.service.impl.IncomingMailTransactionHandlerServiceImpl;
import org.notification.service.incomingmailchain.EmailMatchingChain;
import org.notification.utils.IncomingMailNotificationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This class will try to match the incoming mail by the reference number embedded in the sender
 * name.
 * <p>
 * NOTE : This handler is not used at the moment. But kept here just in case if it was ever needed
 * we just need to hook it back into the chain defined in {@link IncomingMailTransactionHandlerServiceImpl}
 *
 * @author dinuka
 */
@Service
@Qualifier(IncomingMailMatchingChainConstants.SENDER_NAME_REF_MATCHING_HANDLER)
public class SenderNameReferenceMatchingHandler implements EmailMatchingChain {

  private static Logger log = LoggerFactory.getLogger(SenderNameReferenceMatchingHandler.class);

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
        "Trying to find a match in SenderNameReferenceMatchingHandler for subject : {} , Sender: {}",
        transactionInfo.getMessageSubject(), transactionInfo.getSender());
    boolean matchFound = false;
    if (transactionInfo.getEmailHeaders() != null) {
      outer:
      for (Header header : transactionInfo.getEmailHeaders()) {
        if ("To".equalsIgnoreCase(header.getName())) {
          String toSenderHeader = header.getValue();
          /**
           * The regular expression checks for the pattern
           * "Test+referencenumber" <notification@test.one>
           * where we will extract the part within the quotation
           * marks. Some mail clients do not send the quoatation marks
           * so we need to cater to that scenario as well. Also some
           * mail clients put a space after the + sign so we need to
           * cater to that as well.
           */
          Pattern toSenderPattern = Pattern.compile("(\\\")?([A-Za-z\\.\\s\\+0-9]{1,}).*");
          Matcher toSenderMatcher = toSenderPattern.matcher(toSenderHeader);
          if (toSenderMatcher.matches() && toSenderMatcher.groupCount() >= 2) {
            String toHeaderName = toSenderMatcher.group(2);
            log.info("To header value is : {}", toHeaderName);
            String[] toHeaderValueArr = toHeaderName.split("\\+");
            if (toHeaderValueArr.length == 2) {
              String referenceNumber = toHeaderValueArr[1];
              log.info("Searching the Notification Transaction collection by reference number : {}",
                  referenceNumber);
              NotificationTransactionInfo retrievedTransationInfo = transactionInfoRepo
                  .findByReferenceNumber(StringUtils.trimToEmpty(referenceNumber));
              if (retrievedTransationInfo != null) {
                log.info("Match found for the reference number");
                matchFound = true;
                incomingMailUtil.processIncomingMailMatchFound(retrievedTransationInfo,
                    transactionInfo);
                break outer;
              }
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
