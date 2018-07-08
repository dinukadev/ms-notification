package org.notification.service.impl;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.MessageType;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.domain.SMSInfo;
import org.notification.dto.IncomingNotificationDto;
import org.notification.dto.receivesms.ReceiveSMSDataInfoDto;
import org.notification.dto.receivesms.ReceiveSMSMessageInfoDto;
import org.notification.dto.receivesms.ReceiveSMSResponseInfoDto;
import org.notification.repository.IncomingTransactionInfoMongoRepository;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.notification.rest.IntegrationTest;
import org.notification.rest.IntegrationTest.MockRestTemplate;
import org.notification.rest.IntegrationTest.MockSyncTaskExecutor;
import org.notification.service.SMSGatewayPollerService;
import org.notification.utils.PhoneNumberFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {MockRestTemplate.class, MockSyncTaskExecutor.class})
public class SMSGatewayPollerServiceImplTest extends IntegrationTest {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private SMSGatewayPollerService service;

  @Value("${smsConfig.receive.url}")
  private String url;

  @Value("${notification.responseTopic: #{null}}")
  private String incomingSMSNotificationTopic;

  @Value("${smsConfig.markRead.url}")
  private String markAsReadUrl;

  @Autowired
  private NotificationTransactionInfoMongoRepository transRepo;

  @Autowired
  private IncomingTransactionInfoMongoRepository incomingRepo;


  @Before
  public void setup() {
    super.setUp();
    collectionsToBeCleared =
        Arrays.asList(NotificationTransactionInfo.class, IncomingTransactionInfo.class);
  }

  @Test
  public void testCorrelateSMSResponseToSentMessage() {
    String refereneNumber = "123";
    String recipientNumber = "+61457213445";
    String clientId = "ms_messages";
    String messageBody = "test message";
    NotificationStatus sentStatus = NotificationStatus.SENT;

    NotificationTransactionInfo transInfo =
        new NotificationTransactionInfo(clientId, refereneNumber,
            MessageType.SMS, sentStatus, messageBody, DateTime.now(), null, null);
    SMSInfo sms = new SMSInfo(PhoneNumberFormatUtil.formatNumberToAUE164Standard(recipientNumber));
    transInfo.setSmsInfo(sms);
    transRepo.insert(transInfo);

    String timeStamp = "1436174407";
    LocalDateTime smsReceivedDateTime = LocalDateTime
        .fromDateFields(Date.from(Instant.ofEpochSecond(Long.valueOf(timeStamp))));
    ReceiveSMSMessageInfoDto message =
        new ReceiveSMSMessageInfoDto(recipientNumber, "test message", refereneNumber,
            timeStamp);
    ReceiveSMSDataInfoDto data = new ReceiveSMSDataInfoDto(Arrays.asList(message));
    ReceiveSMSResponseInfoDto response =
        new ReceiveSMSResponseInfoDto("200", "SUCCESS", "SUCCESS", data);
    Mockito.when(
        restTemplate.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
            Mockito.eq(new ParameterizedTypeReference<ReceiveSMSResponseInfoDto>() {
            }))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
    Mockito.when(restTemplate
        .exchange(Mockito.eq(markAsReadUrl), Mockito.eq(HttpMethod.PUT), Mockito.anyObject(),
            Mockito.eq(new ParameterizedTypeReference<ReceiveSMSResponseInfoDto>() {
            }))).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    IncomingNotificationDto mailNotifDto =
        new IncomingNotificationDto(clientId, messageBody, recipientNumber,
            refereneNumber, smsReceivedDateTime.toDateTime());

    service.readMessages();


    Mockito.verify(restTemplate, Mockito.times(1))
        .exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
            Mockito.anyObject(),
            Mockito.eq(new ParameterizedTypeReference<ReceiveSMSResponseInfoDto>() {
            }));
    Mockito.verify(restTemplate, Mockito.times(1))
        .exchange(Mockito.eq(markAsReadUrl), Mockito.eq(HttpMethod.PUT),
            Mockito.anyObject(),
            Mockito.eq(new ParameterizedTypeReference<ReceiveSMSResponseInfoDto>() {
            }));
    NotificationTransactionInfo updatedInfo =
        transRepo.findByReferenceNumberAnRecipientNumber(refereneNumber,
            recipientNumber);
    IncomingTransactionInfo incomingInfo = incomingRepo.findBySender(recipientNumber);

    Assert.assertNotNull(updatedInfo);
    Assert.assertEquals(NotificationStatus.RESPONSE_RECEIVED, updatedInfo.getStatus());
    Assert.assertNotNull(incomingInfo);
    Assert.assertNotNull(incomingInfo.getProcessedDate());
  }

}
