package org.notification.repository;

import org.notification.domain.NotificationTransactionInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author dinuka
 */
public interface NotificationTransactionInfoMongoRepository
    extends MongoRepository<NotificationTransactionInfo, String> {

  NotificationTransactionInfo findByReferenceNumber(final String referenceNumber);

  @Query(
      "{ 'emailInfo.toRecipients': ?#{[2]}, 'emailInfo.subject': {$regex: ?0, $options: 'i' }, 'status': ?1 }")
  NotificationTransactionInfo findBySubjectStatusAndSender(final String subject,
                                                           final String status,
                                                           final String sender);

  @Query("{ 'smsInfo.toNumber': ?0 }")
  NotificationTransactionInfo findSMSByNumber(final String recipientNumber);

  @Query("{ 'referenceNumber': ?0, 'smsInfo.toNumber': ?1 }")
  NotificationTransactionInfo findByReferenceNumberAnRecipientNumber(final String referenceNumber,
                                                                     final String recipientNumber);
}
