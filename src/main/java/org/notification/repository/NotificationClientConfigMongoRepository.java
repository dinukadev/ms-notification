package org.notification.repository;

import org.notification.domain.NotificationClientConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author dinuka
 */
public interface NotificationClientConfigMongoRepository
    extends MongoRepository<NotificationClientConfig, String> {

  NotificationClientConfig findByClientId(String clientId);
}
