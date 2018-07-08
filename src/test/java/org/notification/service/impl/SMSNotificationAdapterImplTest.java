package org.notification.service.impl;

import java.util.Arrays;
import java.util.Base64;
import org.junit.Test;
import org.mockito.Mockito;
import org.notification.domain.NotificationRequest;
import org.notification.dto.sendsms.SendSMSDataInfoDto;
import org.notification.dto.sendsms.SendSMSInfoDto;
import org.notification.dto.sendsms.SendSMSMessagesInfoDto;
import org.notification.dto.sendsms.SendSMSResponseInfoDto;
import org.notification.exception.NotificationException;
import org.notification.rest.IntegrationTest;
import org.notification.rest.IntegrationTest.MockRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

/**
 * @author dinuka
 */
@ContextConfiguration(classes = {MockRestTemplate.class})
public class SMSNotificationAdapterImplTest extends IntegrationTest {

  @Autowired
  private SMSNotificationAdapterImpl smsAdapter;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${smsConfig.send.url}")
  private String url;

  @Value("${smsConfig.credentials.userName}")
  private String userName;

  @Value("${smsConfig.credentials.apiKey}")
  private String apiKey;

  @Value("${smsConfig.dedicatedNumber: #{null}}")
  private String dedicatedSenderNumber;

  @SuppressWarnings("unchecked")
  @Test
  public void testSuccessfulSMSTransation() {
    String toNumber = "+61411111111";
    String message = "test";
    String uniqueReference = "124";
    NotificationRequest req = new NotificationRequest.SMSNotificationRequestBuilder()
        .withRecipientNumberMessageAndUniqueReferene(toNumber, message, uniqueReference).build();
    SendSMSMessagesInfoDto smsMessage =
        new SendSMSMessagesInfoDto(toNumber, message, uniqueReference, "SUCCESS",
            dedicatedSenderNumber);
    SendSMSDataInfoDto smsData = new SendSMSDataInfoDto(Arrays.asList(smsMessage));
    SendSMSResponseInfoDto smsResponse =
        new SendSMSResponseInfoDto("200", "SUCCESS", "Successful", smsData);
    Mockito.when(restTemplate.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity(smsResponse, HttpStatus.OK));
    smsAdapter.sendMessage(req);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = NotificationException.class)
  public void testExceptionWhenErrorCodeReceivedFromRestCall() {
    NotificationRequest req = new NotificationRequest.SMSNotificationRequestBuilder()
        .withRecipientNumberMessageAndUniqueReferene(null, null, null).build();
    SendSMSMessagesInfoDto smsMessage = new SendSMSMessagesInfoDto(null, null, null, "SUCCESS",
        dedicatedSenderNumber);
    SendSMSDataInfoDto smsData = new SendSMSDataInfoDto(Arrays.asList(smsMessage));
    SendSMSResponseInfoDto smsResponse =
        new SendSMSResponseInfoDto("400", "SUCCESS", "Successful", smsData);
    Mockito.when(restTemplate.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity(smsResponse, HttpStatus.OK));
    smsAdapter.sendMessage(req);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = NotificationException.class)
  public void testExceptionWhenStatusOnMessageIsNotSuccess() {
    NotificationRequest req = new NotificationRequest.SMSNotificationRequestBuilder()
        .withRecipientNumberMessageAndUniqueReferene(null, null, null).build();
    SendSMSMessagesInfoDto smsMessage =
        new SendSMSMessagesInfoDto(null, null, null, "FAIL", dedicatedSenderNumber);
    SendSMSDataInfoDto smsData = new SendSMSDataInfoDto(Arrays.asList(smsMessage));
    SendSMSResponseInfoDto smsResponse =
        new SendSMSResponseInfoDto("200", "SUCCESS", "Successful", smsData);
    Mockito.when(restTemplate.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity(smsResponse, HttpStatus.BAD_REQUEST));
    smsAdapter.sendMessage(req);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSuccessfulSMSWithFormatterNumberTransaction() {
    String toNumber = "0411111111";
    String message = "test";
    String uniqueReference = "124";
    String e164FormattedNumber = "+61411111111";
    NotificationRequest req = new NotificationRequest.SMSNotificationRequestBuilder()
        .withRecipientNumberMessageAndUniqueReferene(toNumber, message, uniqueReference).build();
    SendSMSMessagesInfoDto smsMessage =
        new SendSMSMessagesInfoDto(toNumber, message, uniqueReference, "SUCCESS",
            dedicatedSenderNumber);
    SendSMSDataInfoDto smsData = new SendSMSDataInfoDto(Arrays.asList(smsMessage));
    SendSMSResponseInfoDto smsResponse =
        new SendSMSResponseInfoDto("200", "SUCCESS", "Successful", smsData);

    SendSMSInfoDto sms = new SendSMSInfoDto();
    sms.addMessage(
        new SendSMSMessagesInfoDto(e164FormattedNumber, message, uniqueReference, null,
            dedicatedSenderNumber));

    HttpHeaders header = new HttpHeaders();
    header.set("Authorization", String.format("Basic %s",
        Base64.getEncoder().encodeToString(String.format("%s:%s", userName, apiKey).getBytes())));

    header.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<SendSMSInfoDto> httpEntity = new HttpEntity<SendSMSInfoDto>(sms, header);
    Mockito.when(
        restTemplate.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.POST), Mockito.eq(httpEntity),
            Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity(smsResponse, HttpStatus.OK));
    smsAdapter.sendMessage(req);
  }

}
