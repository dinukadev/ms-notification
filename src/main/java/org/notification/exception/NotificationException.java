package org.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This common exception will be thrown if there are any exceptions when sending
 * out notifications by the respective notification implementations.
 *
 * @author dinuka
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to send out the notification.")
public class NotificationException extends RuntimeException {

	public NotificationException(final String message) {
		super(message);
	}

	public NotificationException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
