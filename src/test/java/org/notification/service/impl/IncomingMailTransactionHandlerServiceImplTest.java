package org.notification.service.impl;

import java.util.Arrays;
import java.util.Collections;
import javax.mail.Header;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.notification.constants.MSNotificationConstants;
import org.notification.domain.EmailInfo;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.MessageType;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.rest.IntegrationTest;
import org.notification.rest.IntegrationTest.MockSyncTaskExecutor;
import org.notification.service.IncomingTransactionHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author dinuka
 */
@ContextConfiguration(classes = {MockSyncTaskExecutor.class, IntegrationTest.MockJavaMailSender
    .class, IntegrationTest.MockRestTemplate.class})
public class IncomingMailTransactionHandlerServiceImplTest extends IntegrationTest {

  @Autowired
  @Qualifier(MSNotificationConstants.INCOMING_MAIL_PROCESSOR_IMPL_QUALIFIER)
  private IncomingTransactionHandlerService transHandlerService;

  @Value("${notification.responseTopic}")
  private String incomingMailNotificationTopic;

  @Before
  public void setUp() {
    super.setUp();
    collectionsToBeCleared =
        Arrays.asList(NotificationTransactionInfo.class, IncomingTransactionInfo.class);
  }

  @Test
  public void testProcessIncomingMailWhenMatchFound() {
    String email = "test@test.one";
    String subject = "test subject";
    EmailInfo emailInfo = new EmailInfo(Arrays.asList(email), null, null, subject,
        Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        "Test");
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    IncomingTransactionInfo incomingMailInfo = new IncomingTransactionInfo("test reply", subject,
        email, MessageType.EMAIL, DateTime.now(), null);

    mongoTemplate.insert(incomingMailInfo);


    transHandlerService.process(Arrays.asList(incomingMailInfo));

  }

  @Test
  public void testProcessIncomingMailWhenSenderDoesNotMatch() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("patient@test.one"), Collections.emptyList(),
            Collections.emptyList(), "test subject", Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        null);
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    IncomingTransactionInfo incomingMailInfo =
        new IncomingTransactionInfo("test reply", "test subject",
            "other_patient@test.one", MessageType.EMAIL, DateTime.now(), null);

    mongoTemplate.insert(incomingMailInfo);

    transHandlerService.process(Arrays.asList(incomingMailInfo));


  }

  @Test
  public void testProcessIncomingMailWhenSubjectDoesNotMatch() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("patient@test.one"), Collections.emptyList(),
            Collections.emptyList(), "test subject", Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        null);
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    IncomingTransactionInfo incomingMailInfo =
        new IncomingTransactionInfo("test reply", "other subject",
            "patient@test.one", MessageType.EMAIL, DateTime.now(), null);

    mongoTemplate.insert(incomingMailInfo);

    transHandlerService.process(Arrays.asList(incomingMailInfo));


  }

  @Test
  public void testProcessIncomingMailWhenStatusIsNotSent() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("patient@test.one"), Collections.emptyList(),
            Collections.emptyList(), "test subject", Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.FAILED, "test message", DateTime.now(),
        DateTime.now(),
        null);
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    IncomingTransactionInfo incomingMailInfo =
        new IncomingTransactionInfo("test reply", "test subject",
            "patient@test.one", MessageType.EMAIL, DateTime.now(), null);

    mongoTemplate.insert(incomingMailInfo);

    transHandlerService.process(Arrays.asList(incomingMailInfo));


  }

  @Test
  public void testProcessIncomingMailWhenMatchFoundWithSubjectInCaps() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("patient@test.one"), Collections.emptyList(),
            Collections.emptyList(), "TEST SUBJECT", Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        null);
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    IncomingTransactionInfo incomingMailInfo =
        new IncomingTransactionInfo("test reply", "test subject",
            "patient@test.one", MessageType.EMAIL, DateTime.now(), null);

    mongoTemplate.insert(incomingMailInfo);

    transHandlerService.process(Arrays.asList(incomingMailInfo));


  }

  @Test
  public void testProcessIncomingMailWhenMatchFoundWithReferencesHeader() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("patient@test.one"), Collections.emptyList(),
            Collections.emptyList(), "test subject", Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        null);
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    Header referencesHeader = new Header("References", "<123>\r\n<dummy value>");

    IncomingTransactionInfo incomingMailInfo =
        new IncomingTransactionInfo("test reply", "dummy subject",
            "patient@test.one", MessageType.EMAIL, DateTime.now(),
            Arrays.asList(referencesHeader));

    mongoTemplate.insert(incomingMailInfo);

    transHandlerService.process(Arrays.asList(incomingMailInfo));


  }

  @Test
  public void testProcessIncomingMailWhenMatchFoundWithReferencesHeaderWithSpace() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("patient@test.one"), Collections.emptyList(),
            Collections.emptyList(), "test subject", Collections.emptyList());
    NotificationTransactionInfo sentInfo = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        null);
    sentInfo.setEmailInfo(emailInfo);
    mongoTemplate.insert(sentInfo);

    Header referencesHeader = new Header("References", "< 123 >\r\n <dummy value> ");

    IncomingTransactionInfo incomingMailInfo =
        new IncomingTransactionInfo("test reply", "dummy subject",
            "patient@test.one", MessageType.EMAIL, DateTime.now(),
            Arrays.asList(referencesHeader));

    mongoTemplate.insert(incomingMailInfo);

    transHandlerService.process(Arrays.asList(incomingMailInfo));


  }
}
