package org.notification.rest.dto.assembler;

import org.notification.rest.dto.NotificationResponseDto;

/**
 * @author dinuka
 */
public class NotificationResponseDtoAssembler {

  public static NotificationResponseDto assemble(final String referenceNumber, String status,
                                                 String clientId) {
    NotificationResponseDto responseDto =
        new NotificationResponseDto(referenceNumber, status, clientId);
    return responseDto;
  }
}
