package org.notification.rest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import javax.mail.internet.MimeMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.notification.Application;
import org.notification.builder.EmailNotificationRequestDtoBuilder;
import org.notification.builder.SMSNotificationRequestDtoBuilder;
import org.notification.constants.MSNotificationConstants;
import org.notification.domain.MessageType;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.dto.Dto;
import org.notification.dto.sendsms.SendSMSDataInfoDto;
import org.notification.dto.sendsms.SendSMSResponseInfoDto;
import org.notification.exception.NotificationException;
import org.notification.repository.NotificationTransactionInfoMongoRepository;
import org.notification.rest.dto.EmailAttachmentInfoDto;
import org.notification.rest.dto.EmailNotificationRequestDto;
import org.notification.rest.dto.NotificationResponseDto;
import org.notification.rest.dto.SMSNotificationRequestDto;
import org.notification.utils.PhoneNumberFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ContextConfiguration(
    classes = {IntegrationTest.MockJavaMailSender.class, IntegrationTest.MockRestTemplate.class})
@SuppressWarnings("unchecked")
public class NotificationIntegrationTest extends IntegrationTest {

  @Autowired
  private NotificationTransactionInfoMongoRepository notificationRepo;

  @Autowired
  private Application.ClientConfiguration clientConfig;

  private String testClientId;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${smsConfig.send.url}")
  private String smsUrl;

  @Before
  public void setUp() {
    super.setUp();
    for (String clientId : clientConfig.getClientConfig().keySet()) {
      testClientId = clientId;
      break;
    }
    collectionsToBeCleared = Arrays.asList(NotificationTransactionInfo.class);
  }

  @Test
  public void sendEmailSyncSuccessWithPlainText() throws Exception {
    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
    String sender = "Test";
    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .withSender(sender)
        .build();
    String content = mapper.writeValueAsString(emailRequest);
    String response = mockMvc.perform(MockMvcRequestBuilders
        .post(MSNotificationConstants.API_VERSION_1 +
            MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
        .getResponse().getContentAsString();

    Dto<NotificationResponseDto> actual = mapper.readValue(response, Dto.class);
    NotificationResponseDto responseDto = mapper.convertValue(actual.getData(),
        NotificationResponseDto.class);
    String referenceNumber = responseDto.getReferenceNumber();
    NotificationTransactionInfo persistedInfo = notificationRepo
        .findByReferenceNumber(referenceNumber);
    Assert.assertEquals(NotificationStatus.SENT, persistedInfo.getStatus());
    Assert.assertEquals(sender, persistedInfo.getSender());
  }

  @Test
  public void sendEmailSyncSuccessWithHtmlText() throws Exception {

    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .withMessage("<html><body>Test message</body></html>")
        .build();
    String content = mapper.writeValueAsString(emailRequest);
    String response = mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
        .getResponse().getContentAsString();

    Dto<NotificationResponseDto> actual = mapper.readValue(response, Dto.class);
    NotificationResponseDto responseDto = mapper.convertValue(actual.getData(),
        NotificationResponseDto.class);

    String referenceNumber = responseDto.getReferenceNumber();

    NotificationTransactionInfo persistedInfo = notificationRepo
        .findByReferenceNumber(referenceNumber);

    Assert.assertEquals(NotificationStatus.SENT, persistedInfo.getStatus());
  }

  @Test
  public void responseShouldBeInvalidWhenNoClientIdPassed() throws Exception {
    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .build();
    String content = mapper.writeValueAsString(emailRequest);
    mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .content(content)).andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  public void statusFailedWhenEmailCommunicationFailed() throws Exception {
    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
    Mockito.doThrow(new NotificationException("")).when(mailSenderMock).send(mimeMessage);

    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .build();
    String content = mapper.writeValueAsString(emailRequest);

    mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError());

    NotificationTransactionInfo notif = mongoTemplate.findOne(
        new Query(Criteria.where("emailInfo.toRecipients")
            .is(emailRequest.getToRecipients().get(0))),
        NotificationTransactionInfo.class);

    Assert.assertEquals(NotificationStatus.FAILED, notif.getStatus());
  }

  @Test
  public void sendEmailAsynchrously() throws Exception {
    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .build();
    String content = mapper.writeValueAsString(emailRequest);

    String response = mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_ASYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
        .getResponse().getContentAsString();

    Dto<NotificationResponseDto> actual = mapper.readValue(response, Dto.class);
    NotificationResponseDto responseDto = mapper.convertValue(actual.getData(),
        NotificationResponseDto.class);

    String referenceNumber = responseDto.getReferenceNumber();
    Assert.assertNotNull(referenceNumber);
  }

  @Test
  public void unauthorisedResponseWhenClientIdNotPresentOnHttpHeader() throws Exception {
    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .build();
    String content = mapper.writeValueAsString(emailRequest);
    mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(content))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  public void invalidAttachmentThrownWhenByteStreamInvalid() throws Exception {
    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

    EmailAttachmentInfoDto attDto = new EmailAttachmentInfoDto(
        "invalid byte stream", "invalidfile.pdf");

    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .withEmailAttachments(Arrays.asList(attDto))
        .build();

    String content = mapper.writeValueAsString(emailRequest);
    mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError());
  }

  @Test
  public void validAttachmentDetailsSent() throws Exception {
    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

    Resource resource = new ClassPathResource("testpdf.pdf");
    byte[] byteArr = Files.readAllBytes(Paths.get(resource.getURI()));
    String base64Encoded = Base64.getEncoder().encodeToString(byteArr);

    EmailAttachmentInfoDto attDto = new EmailAttachmentInfoDto(base64Encoded,
        "testpdf.pdf");

    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .withEmailAttachments(Arrays.asList(attDto))
        .build();
    String content = mapper.writeValueAsString(emailRequest);
    String response = mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn()
        .getResponse().getContentAsString();

    Dto<NotificationResponseDto> actual = mapper.readValue(response, Dto.class);
    NotificationResponseDto responseDto = mapper.convertValue(actual.getData(),
        NotificationResponseDto.class);

    String referenceNumber = responseDto.getReferenceNumber();

    NotificationTransactionInfo persistedInfo = notificationRepo
        .findByReferenceNumber(referenceNumber);

    Assert.assertEquals(NotificationStatus.SENT, persistedInfo.getStatus());
  }

  @Test
  public void invalidAttachmentWhenFileNameIsNotSent() throws Exception {
    JavaMailSender mailSenderMock = webApplicationContext.getBean(JavaMailSender.class);
    MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
    Mockito.when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

    Resource resource = new ClassPathResource("testpdf.pdf");
    byte[] byteArr = Files.readAllBytes(Paths.get(resource.getURI()));
    String base64Encoded = Base64.getEncoder().encodeToString(byteArr);

    EmailAttachmentInfoDto attDto = new EmailAttachmentInfoDto(base64Encoded, null);

    EmailNotificationRequestDto emailRequest = EmailNotificationRequestDtoBuilder
        .anEmailNotificationRequestDto()
        .randomPopulated()
        .withEmailAttachments(Arrays.asList(attDto))
        .build();

    String content = mapper.writeValueAsString(emailRequest);
    mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDEMAIL)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        .header("Client-Id", testClientId).content(content))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError());
  }

  @Test
  public void sendSMSSyncSuccessful() throws Exception {
    SMSNotificationRequestDto sms =
        SMSNotificationRequestDtoBuilder.aSMSNotificationRequestDto()
            .randomPopulated().build();
    String content = mapper.writeValueAsString(sms);
    SendSMSResponseInfoDto smsResponse = new SendSMSResponseInfoDto("200",
        "SUCCESS", "Successful",
        new SendSMSDataInfoDto(Collections.emptyList()));
    Mockito.when(restTemplate.exchange(Mockito.anyString(),
        Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.any(ParameterizedTypeReference.class)))
        .thenReturn(new ResponseEntity(smsResponse, HttpStatus.OK));
    String responseContent = mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDSMS)
        .contentType(MediaType.APPLICATION_JSON_VALUE).header("Client-Id", testClientId)
        .content(content)).andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn().getResponse().getContentAsString();
    Dto<NotificationResponseDto> response = mapper.readValue(responseContent, Dto.class);
    NotificationResponseDto responseDto = mapper.convertValue(response.getData(),
        NotificationResponseDto.class);
    Assert.assertNotNull(responseDto);
    String referenceNumber = responseDto.getReferenceNumber();
    NotificationTransactionInfo persistedInfo = notificationRepo
        .findByReferenceNumber(referenceNumber);
    Assert.assertNotNull(persistedInfo);
    Assert.assertEquals(MessageType.SMS, persistedInfo.getMessageType());
    Assert.assertEquals(NotificationStatus.SENT, persistedInfo.getStatus());
  }

  @Test
  public void sendSMSSyncUnsuccessful() throws Exception {
    String recipientNumber = "0457223443";
    SMSNotificationRequestDto sms =
        SMSNotificationRequestDtoBuilder.aSMSNotificationRequestDto()
            .randomPopulated()
            .withToNumber(recipientNumber)
            .build();
    String content = mapper.writeValueAsString(sms);
    Mockito.when(restTemplate.exchange(Mockito.anyString(),
        Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.any(ParameterizedTypeReference.class))).thenThrow(RestClientException.class);
    mockMvc.perform(post(MSNotificationConstants.API_VERSION_1 +
        MSNotificationConstants.API_SYNC_SENDSMS)
        .contentType(MediaType.APPLICATION_JSON_VALUE).header("Client-Id", testClientId)
        .content(content)).andExpect(MockMvcResultMatchers.status().isInternalServerError());
    NotificationTransactionInfo persistedInfo = notificationRepo
        .findSMSByNumber(PhoneNumberFormatUtil.formatNumberToAUE164Standard(recipientNumber));
    Assert.assertNotNull(persistedInfo);
    Assert.assertEquals(NotificationStatus.FAILED, persistedInfo.getStatus());
  }
}
