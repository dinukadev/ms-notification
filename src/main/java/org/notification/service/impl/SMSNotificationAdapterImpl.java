package org.notification.service.impl;

import java.util.Base64;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.notification.constants.MSNotificationConstants;
import org.notification.constants.ProviderType;
import org.notification.domain.NotificationRequest;
import org.notification.dto.sendsms.SendSMSInfoDto;
import org.notification.dto.sendsms.SendSMSMessagesInfoDto;
import org.notification.dto.sendsms.SendSMSResponseInfoDto;
import org.notification.exception.NotificationException;
import org.notification.service.NotificationAdapter;
import org.notification.utils.PhoneNumberFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component(ProviderType.SMS)
public class SMSNotificationAdapterImpl implements NotificationAdapter {

  private static Logger log = LoggerFactory.getLogger(SMSNotificationAdapterImpl.class);

  @Value("${smsConfig.send.url}")
  private String url;

  @Value("${smsConfig.credentials.userName}")
  private String userName;

  @Value("${smsConfig.credentials.apiKey}")
  private String apiKey;

  // This number is purchased from clicksend.com as a dedicated number for
  // this account.
  @Value("${smsConfig.dedicatedNumber: #{null}}")
  private String dedicatedSenderNumber;

  private String hashedCredentials;

  @Autowired
  private RestTemplate restTemplate;

  @PostConstruct
  public void init() {
    // We base 64 encode the userName:apiKey combination as this needs to be
    // set in the 'Authorization' header when calling the REST API to send
    // the SMS.
    hashedCredentials = Base64.getEncoder().encodeToString(String.format("%s:%s",
        userName, apiKey).getBytes());
  }

  @Override
  public void sendMessage(NotificationRequest notificationRequest) {
    HttpHeaders header = new HttpHeaders();
    header.set("Authorization", String.format("Basic %s", hashedCredentials));
    header.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    SendSMSInfoDto sms = new SendSMSInfoDto();

    String e164FormatterPhoneNumber = PhoneNumberFormatUtil
        .formatNumberToAUE164Standard(notificationRequest.getToNumber());
    sms.addMessage(new SendSMSMessagesInfoDto(e164FormatterPhoneNumber,
        notificationRequest.getMessage(),
        notificationRequest.getUniqueReference(), null, dedicatedSenderNumber));

    HttpEntity<SendSMSInfoDto> httpEntity = new HttpEntity(sms, header);

    log.info("Sending SMS to : {}", notificationRequest.getToNumber());
    ResponseEntity<SendSMSResponseInfoDto> response;
    try {
      response = restTemplate.exchange(url, HttpMethod.POST, httpEntity,
          new ParameterizedTypeReference<SendSMSResponseInfoDto>() {
          });
    } catch (RestClientException e) {
      log.error("Exception raised while calling the SMS REST endpoint : {}", url);
      throw new NotificationException("Exception raised while calling the SMS REST endpoint.");
    }

    if (response == null || response.getBody() == null) {
      throw new NotificationException("Error response received while trying to send out the SMS");
    }

    SendSMSResponseInfoDto smsResponse = response.getBody();
    log.info("SMS message response : {}", smsResponse);
    if (!String.valueOf(HttpStatus.OK.value()).equalsIgnoreCase(smsResponse.getHttpCode())) {
      log.error("Error response received while sending the SMS to : {} for reference : {}",
          notificationRequest.getToNumber(), notificationRequest.getUniqueReference());
      throw new NotificationException("Error response received while trying to send out the SMS");
    }

    if (smsResponse.getData() != null && !smsResponse.getData().getMessages().isEmpty()) {
      for (SendSMSMessagesInfoDto smsMessage : smsResponse.getData().getMessages()) {
        if (!MSNotificationConstants.RESPONSE_SUCCESS
            .equalsIgnoreCase(StringUtils.trimToEmpty(smsMessage.getStatus()))) {
          log.error("Error response received while trying to send an SMS to the number : {}",
              smsMessage.getToNumber());
          throw new NotificationException("Unable to send out the SMS");
        }
      }
    }

  }

}
