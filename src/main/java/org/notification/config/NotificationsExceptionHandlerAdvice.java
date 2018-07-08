package org.notification.config;

import org.notification.exception.InvalidEmailAttachmentException;
import org.notification.exception.InvalidRequestException;
import org.notification.exception.NotificationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The exception handler will generate the appropriate HTTP codes depending on the exception case.
 *
 * @author dinuka
 */
@ControllerAdvice(annotations = RestController.class)
public class NotificationsExceptionHandlerAdvice {

  @ExceptionHandler(InvalidEmailAttachmentException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public void handleInvalidEmailAttachmentExceptions() {
  }

  @ExceptionHandler(InvalidRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleInvalidRequestExceptions() {
  }

  @ExceptionHandler(NotificationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleNotificationException() {
  }
}
