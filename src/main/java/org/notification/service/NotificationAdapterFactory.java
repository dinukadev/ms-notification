package org.notification.service;

/**
 *
 * @author dinuka
 *
 */
public interface NotificationAdapterFactory {

	NotificationAdapter getAdapter(final String system);
}
