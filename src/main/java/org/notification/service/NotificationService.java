package org.notification.service;

import org.notification.domain.NotificationTransactionInfo;
import org.notification.rest.dto.NotificationResponseDto;

/**
 *
 * @author dinuka
 *
 */
public interface NotificationService {

	NotificationResponseDto sendEmailSync(final NotificationTransactionInfo notificationTransactionInfo);

	void sendEmailAsync(final NotificationTransactionInfo notificationTransactionInfo);

	NotificationResponseDto sendSmsSync(final NotificationTransactionInfo notificationTransactionInfo);

	void sendSMSAsync(final NotificationTransactionInfo notificationTransactionInfo);
}
