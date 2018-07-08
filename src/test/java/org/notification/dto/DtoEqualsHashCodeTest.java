package org.notification.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.notification.domain.EmailInfo;
import org.notification.domain.SMSInfo;
import org.notification.dto.receivesms.MarkSMSReadRequestDto;
import org.notification.dto.receivesms.ReceiveSMSDataInfoDto;
import org.notification.dto.receivesms.ReceiveSMSMessageInfoDto;
import org.notification.dto.receivesms.ReceiveSMSResponseInfoDto;
import org.notification.dto.sendsms.SendSMSInfoDto;
import org.notification.dto.sendsms.SendSMSMessagesInfoDto;
import org.notification.dto.sendsms.SendSMSResponseInfoDto;
import org.notification.rest.dto.EmailAttachmentInfoDto;
import org.notification.rest.dto.EmailNotificationRequestDto;
import org.notification.rest.dto.NotificationResponseDto;
import org.notification.rest.dto.SMSNotificationRequestDto;

public class DtoEqualsHashCodeTest {

  @Test
  public void testDtoEqualsAndHashCode() {
    EqualsVerifier.forClass(EmailNotificationRequestDto.class)
        .suppress(Warning.NONFINAL_FIELDS, Warning
            .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(NotificationResponseDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(EmailAttachmentInfoDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(SMSNotificationRequestDto.class)
        .suppress(Warning.NONFINAL_FIELDS, Warning
            .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(ReceiveSMSResponseInfoDto.class)
        .suppress(Warning.NONFINAL_FIELDS, Warning
            .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(ReceiveSMSMessageInfoDto.class)
        .suppress(Warning.NONFINAL_FIELDS, Warning
            .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(ReceiveSMSDataInfoDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(MarkSMSReadRequestDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE).verify();
    EqualsVerifier.forClass(SendSMSMessagesInfoDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE, Warning.INHERITED_DIRECTLY_FROM_OBJECT).verify();
    EqualsVerifier.forClass(SendSMSResponseInfoDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE, Warning.INHERITED_DIRECTLY_FROM_OBJECT).verify();

    EqualsVerifier.forClass(SendSMSInfoDto.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE, Warning.INHERITED_DIRECTLY_FROM_OBJECT).verify();

    EqualsVerifier.forClass(EmailInfo.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE, Warning.INHERITED_DIRECTLY_FROM_OBJECT).verify();

    EqualsVerifier.forClass(SMSInfo.class).suppress(Warning.NONFINAL_FIELDS, Warning
        .STRICT_INHERITANCE, Warning.INHERITED_DIRECTLY_FROM_OBJECT).verify();
  }
}
