package org.notification.service.impl;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.notification.constants.MSNotificationConstants;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.MessageType;
import org.notification.dto.receivesms.MarkSMSReadRequestDto;
import org.notification.dto.receivesms.ReceiveSMSMessageInfoDto;
import org.notification.dto.receivesms.ReceiveSMSResponseInfoDto;
import org.notification.repository.IncomingTransactionInfoMongoRepository;
import org.notification.service.IncomingTransactionHandlerService;
import org.notification.service.SMSGatewayPollerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author dinuka
 */
@Component
public class SMSGatewayPollerServiceImpl implements SMSGatewayPollerService {

  private static Logger log = LoggerFactory.getLogger(SMSGatewayPollerServiceImpl.class);

  @Value("${smsConfig.receive.url}")
  private String url;

  @Value("${smsConfig.credentials.userName}")
  private String userName;

  @Value("${smsConfig.credentials.apiKey}")
  private String apiKey;

  @Value("${smsConfig.markRead.url}")
  private String markAsReadUrl;

  private String hashedCredentials;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private IncomingTransactionInfoMongoRepository incomingTransInfoRepo;

  @Autowired
  @Qualifier(MSNotificationConstants.INCOMING_SMS_PROCESSOR_IMPL_QUALIFIER)
  private IncomingTransactionHandlerService incomingTransactionHandler;

  @PostConstruct
  public void init() {
    // We base 64 encode the userName:apiKey combination as this needs to be
    // set in the 'Authorization' header when calling the REST API to send
    // the SMS.
    hashedCredentials = Base64.getEncoder().encodeToString(String.format("%s:%s",
        userName, apiKey).getBytes());
  }

  @Override
  public void readMessages() {
    HttpHeaders header = new HttpHeaders();
    header.set("Authorization", String.format("Basic %s", hashedCredentials));
    HttpEntity<String> httpEntity = new HttpEntity("", header);
    try {
      log.info("Calling the REST API to retrieve all SMS messages.");
      ResponseEntity<ReceiveSMSResponseInfoDto> response = restTemplate.exchange(
          url, HttpMethod.GET, httpEntity,
          new ParameterizedTypeReference<ReceiveSMSResponseInfoDto>() {
          });
      long dateBefore = Instant.now().getEpochSecond();
      // Mark the messages as read as soon as they are read. If there is
      // an error then we will start over again reading the messages
      // before we start processing them.
      markMessagesAsRead(dateBefore);
      List<IncomingTransactionInfo> incomingMessageList = new LinkedList<>();
      if (response != null && response.getBody() != null) {
        ReceiveSMSResponseInfoDto smsResponse = response.getBody();
        if (smsResponse.getData() != null && CollectionUtils.isNotEmpty(
            smsResponse.getData().getMessages())) {
          log.info("SMS messages exists. Total number of messages : {}",
              smsResponse.getData().getMessages().size());
          for (ReceiveSMSMessageInfoDto receivedMessage :
              smsResponse.getData().getMessages()) {
            // The REST API sends a UNIX timestamp value
            String timeStamp = receivedMessage.getTimeStamp();
            DateTime receivedDate = DateTime.now();
            if (StringUtils.isNotEmpty(timeStamp)) {
              try {
                receivedDate = new DateTime(Date.from(
                    Instant.ofEpochSecond(Long.valueOf(timeStamp))));
              } catch (NumberFormatException e) {
                // Do nothing because we do not want to fail
                // this transaction just because the timestamp
                // was invalid. In this case, we will use the
                // current date and time as the received
                // date/time.
              }
            }
            IncomingTransactionInfo incomingTransInfo = new IncomingTransactionInfo(
                receivedMessage.getBody(), receivedMessage.getFrom(),
                MessageType.SMS, receivedDate,
                receivedMessage.getReferenceNumber());
            incomingTransInfoRepo.insert(incomingTransInfo);
            incomingMessageList.add(incomingTransInfo);
          }
          incomingTransactionHandler.process(incomingMessageList);
        }
      }

    } catch (RestClientException e) {
      log.error("Exception received while retrieving messages from the SMS gateway : {}", e);
    } catch (Exception e) {
      String exceptionStr = String.format("Exception trying Read messages " +
          "and persist transaction in DB");
      log.error(exceptionStr, e);
      throw new RuntimeException(exceptionStr);
    }
  }

  private void markMessagesAsRead(long dateBefore) {
    HttpHeaders header = new HttpHeaders();
    header.set("Authorization", String.format("Basic %s", hashedCredentials));
    header.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    MarkSMSReadRequestDto markAsRead = new MarkSMSReadRequestDto(String.valueOf(dateBefore));
    HttpEntity<MarkSMSReadRequestDto> httpEntity = new HttpEntity(markAsRead, header);
    restTemplate.exchange(markAsReadUrl, HttpMethod.PUT, httpEntity,
        new ParameterizedTypeReference<ReceiveSMSResponseInfoDto>() {
        });
  }
}
