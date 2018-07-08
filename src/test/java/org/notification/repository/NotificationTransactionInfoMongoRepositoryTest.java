package org.notification.repository;

import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.notification.domain.EmailInfo;
import org.notification.domain.MessageType;
import org.notification.domain.NotificationStatus;
import org.notification.domain.NotificationTransactionInfo;
import org.notification.domain.SMSInfo;
import org.notification.rest.IntegrationTest;
import org.notification.rest.IntegrationTest.MockJavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {MockJavaMailSender.class, IntegrationTest.MockRestTemplate.class})
public class NotificationTransactionInfoMongoRepositoryTest extends IntegrationTest {

  @Autowired
  private NotificationTransactionInfoMongoRepository transRepo;

  @Before
  public void setup() {
    collectionsToBeCleared = Arrays.asList(NotificationTransactionInfo.class);
  }

  @Test
  public void testFindBySubjectStatusAndSender() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("test@test.com"), null, null, "test subject",
            Collections.emptyList());
    NotificationTransactionInfo expected = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        "Test");
    expected.setEmailInfo(emailInfo);
    mongoTemplate.insert(expected);

    NotificationTransactionInfo actual = transRepo.findBySubjectStatusAndSender("Test subject",
        NotificationStatus.SENT.name(), "test@test.com");

    Assert.assertThat(actual, SamePropertyValuesAs.samePropertyValuesAs(expected));
  }

  @Test
  public void testFindBySubjectStatusAndSenderWhenToListHasOtherAddresses() {
    EmailInfo emailInfo =
        new EmailInfo(Arrays.asList("test@test.com", "test2test.one"), null, null,
            "test subject", Collections.emptyList());
    NotificationTransactionInfo expected = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.EMAIL, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        "Test");
    expected.setEmailInfo(emailInfo);
    mongoTemplate.insert(expected);

    NotificationTransactionInfo actual = transRepo.findBySubjectStatusAndSender("test subject",
        NotificationStatus.SENT.name(), "test@test.com");

    Assert.assertThat(actual, SamePropertyValuesAs.samePropertyValuesAs(expected));
  }

  @Test
  public void testFindSMSSentByNumber() {
    SMSInfo smsInfo = new SMSInfo("1234");
    NotificationTransactionInfo expected = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.SMS, NotificationStatus.SENT, "test message", DateTime.now(), DateTime.now(),
        null);
    expected.setSmsInfo(smsInfo);
    mongoTemplate.insert(expected);

    NotificationTransactionInfo actual = transRepo.findSMSByNumber(smsInfo.getToNumber());

    Assert.assertThat(actual, SamePropertyValuesAs.samePropertyValuesAs(expected));
  }

  @Test
  public void testFindByReferenceNumberAndRecipientNumber() {
    SMSInfo smsInfo = new SMSInfo("1234");
    NotificationStatus sentStatus = NotificationStatus.SENT;
    NotificationTransactionInfo expected = new NotificationTransactionInfo("ms_test_request", "123",
        MessageType.SMS, sentStatus, "test message", DateTime.now(), DateTime.now(),
        null);
    expected.setSmsInfo(smsInfo);
    mongoTemplate.insert(expected);

    NotificationTransactionInfo actual =
        transRepo.findByReferenceNumberAnRecipientNumber("123", "1234");
    Assert.assertThat(actual, SamePropertyValuesAs.samePropertyValuesAs(expected));
  }

}
