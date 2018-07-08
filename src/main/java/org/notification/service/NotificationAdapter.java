package org.notification.service;

import org.notification.domain.NotificationRequest;

/**
 * This will be the common adapter that will be used by specific notification
 * implementations.
 *
 * @author dinuka
 *
 */
public interface NotificationAdapter {

	void sendMessage(NotificationRequest notificationRequest);
}
