package org.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author dinuka
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Invalid attachment details sent.")
public class InvalidEmailAttachmentException extends RuntimeException {

	public InvalidEmailAttachmentException(final String message) {
		super(message);
	}

	public InvalidEmailAttachmentException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
