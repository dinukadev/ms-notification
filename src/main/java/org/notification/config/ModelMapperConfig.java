package org.notification.config;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.notification.domain.EmailAttachmentInfo;
import org.notification.domain.EmailInfo;
import org.notification.domain.MessageType;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.domain.SMSInfo;
import org.notification.rest.dto.EmailAttachmentInfoDto;
import org.notification.rest.dto.EmailNotificationRequestDto;
import org.notification.rest.dto.SMSNotificationRequestDto;
import org.notification.utils.PhoneNumberFormatUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class configures the model mapper which will convert objects from one type to another.
 *
 * @author dinuka
 */
@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    mapper.getConfiguration().setFieldMatchingEnabled(true);
    mapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE);
    Converter<EmailNotificationRequestDto, NotificationTransactionInfo>
        defaultValueConvertedForEmail = (
        context) -> {
      NotificationTransactionInfo info = context.getDestination();
      EmailNotificationRequestDto emailRequestDto = context.getSource();
      if (info.getStatus() == null) {
        info.setStatus(NotificationStatus.CREATED);
      }
      if (StringUtils.isEmpty(info.getReferenceNumber())) {
        info.setReferenceNumber(new ObjectId(new Date()).toString());
      }
      if (info.getReceivedDate() == null) {
        info.setReceivedDate(DateTime.now());
      }
      if (info.getMessagType() == null) {
        info.setMessageType(MessageType.EMAIL);
      }

      /**
       * Not the nicest way to do this, but ModelMapper does not have a
       * way of mapping lists with complex types embedded within an
       * object.
       */
      List<EmailAttachmentInfoDto> source = emailRequestDto.getEmailAttachments();
      List<EmailAttachmentInfo> emailAttachmentsList = new LinkedList<>();
      if (CollectionUtils.isNotEmpty(source)) {
        emailAttachmentsList = source.stream().map(att -> {
          return new EmailAttachmentInfo(att.getEncodedByteStream(), att.getFileName());

        }).collect(Collectors.toList());
      }

      EmailInfo emailInfo =
          new EmailInfo(emailRequestDto.getToRecipients(), emailRequestDto.getCcRecipients(),
              emailRequestDto.getBccRecipients(), emailRequestDto.getSubject(),
              emailAttachmentsList);
      info.setEmailInfo(emailInfo);
      return info;

    };

    mapper.typeMap(EmailNotificationRequestDto.class, NotificationTransactionInfo.class)
        .addMappings(m -> m.skip(NotificationTransactionInfo::setId))
        .addMappings(m -> m.skip(NotificationTransactionInfo::setClientId))
        .addMappings(m -> m.skip(NotificationTransactionInfo::setEmailInfo))
        .addMappings(m -> m.skip(NotificationTransactionInfo::setSmsInfo))
        .setPostConverter(defaultValueConvertedForEmail);

    Converter<SMSNotificationRequestDto, NotificationTransactionInfo> defaultConverterForSMS =
        (context) -> {

          NotificationTransactionInfo destination = context.getDestination();
          SMSNotificationRequestDto source = context.getSource();
          if (destination.getStatus() == null) {
            destination.setStatus(NotificationStatus.CREATED);
          }
          if (StringUtils.isEmpty(destination.getReferenceNumber())) {
            destination.setReferenceNumber(new ObjectId(new Date()).toString());
          }
          if (destination.getReceivedDate() == null) {
            destination.setReceivedDate(DateTime.now());
          }
          if (destination.getMessagType() == null) {
            destination.setMessageType(MessageType.SMS);
          }

          // We always store the phone number in the E164 format no matter
          // the format we receive from the user.
          String e164FormatterNumber =
              PhoneNumberFormatUtil.formatNumberToAUE164Standard(source.getToNumber());
          SMSInfo smsInfo = new SMSInfo(e164FormatterNumber);
          destination.setSmsInfo(smsInfo);
          return destination;
        };
    mapper.typeMap(SMSNotificationRequestDto.class, NotificationTransactionInfo.class)
        .addMappings(m -> m.skip(NotificationTransactionInfo::setEmailInfo))
        .addMappings(m -> m.skip(NotificationTransactionInfo::setSmsInfo))
        .addMappings(m -> m.skip(NotificationTransactionInfo::setId))
        .addMappings(m -> m.skip(NotificationTransactionInfo::setClientId))
        .setPostConverter(defaultConverterForSMS);
    return mapper;
  }

}
