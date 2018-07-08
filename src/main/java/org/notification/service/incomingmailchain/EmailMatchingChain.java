package org.notification.service.incomingmailchain;

import org.notification.domain.IncomingTransactionInfo;
import org.notification.service.incomingmailchain.impl.ReferenceHeaderMatchingHandler;
import org.notification.service.incomingmailchain.impl.SenderNameReferenceMatchingHandler;
import org.notification.service.incomingmailchain.impl.SubjectMatchingHandler;

/**
 * <p>
 * This interface defines the handle chain that will provide the functionality of finding a match of
 * the incoming mail to the outgoing mail already sent out.
 * </p>
 *
 * <p>
 * There are three different handle chains that will try to find a match. They are as follows;
 * </p>
 *
 * <ul>
 * <li>{@link SenderNameReferenceMatchingHandler}</li>
 * <li>{@link ReferenceHeaderMatchingHandler}</li>
 * <li>{@link SubjectMatchingHandler}</li>
 * </ul>
 *
 * @author dinuka
 */
public interface EmailMatchingChain {

  /**
   * This method will take in the next handler implementation in the chain that the call needs to be
   * delegated to.
   *
   * @param next
   */
  public void next(EmailMatchingChain next);

  /**
   * This method will handle trying to match the incoming mail to the already sent mail depending on
   * conditions that are relevant to the handler.
   *
   * @param transactioInfo
   */
  public void handle(IncomingTransactionInfo transactioInfo);
}
