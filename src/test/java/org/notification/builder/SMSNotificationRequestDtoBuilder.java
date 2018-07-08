package org.notification.builder;

import org.apache.commons.lang3.RandomStringUtils;
import org.notification.rest.dto.SMSNotificationRequestDto;


public class SMSNotificationRequestDtoBuilder {

  private String toNumber;

  private String message;

  private SMSNotificationRequestDtoBuilder() {
  }

  public static SMSNotificationRequestDtoBuilder aSMSNotificationRequestDto() {
    return new SMSNotificationRequestDtoBuilder();
  }

  public SMSNotificationRequestDtoBuilder withToNumber(String toNumber) {
    this.toNumber = toNumber;
    return this;
  }

  public SMSNotificationRequestDtoBuilder withMessage(String message) {
    this.message = message;
    return this;
  }

  public SMSNotificationRequestDtoBuilder randomPopulated() {
    this.toNumber = RandomStringUtils.randomNumeric(3);
    this.message = RandomStringUtils.randomAlphabetic(3);
    return this;
  }

  public SMSNotificationRequestDto build() {
    SMSNotificationRequestDto sMSNotificationRequestDto =
        new SMSNotificationRequestDto(toNumber, message);
    return sMSNotificationRequestDto;
  }
}
