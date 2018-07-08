package org.notification.repository;

import org.notification.domain.IncomingTransactionInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author dinuka
 */
public interface IncomingTransactionInfoMongoRepository
    extends MongoRepository<IncomingTransactionInfo, String> {

  IncomingTransactionInfo findBySender(final String sender);
}
