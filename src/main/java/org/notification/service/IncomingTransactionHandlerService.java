package org.notification.service;

import java.util.List;
import org.notification.domain.IncomingTransactionInfo;

/**
 * This interface will need to be implemented by services that handle different incoming
 * transactions such as email, sms etc.
 *
 * @author dinuka
 */
public interface IncomingTransactionHandlerService {

  void process(List<IncomingTransactionInfo> incomingTransactionList);
}
