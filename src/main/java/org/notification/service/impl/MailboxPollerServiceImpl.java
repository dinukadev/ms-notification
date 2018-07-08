package org.notification.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.joda.time.DateTime;
import org.notification.constants.MSNotificationConstants;
import org.notification.domain.IncomingTransactionInfo;
import org.notification.domain.MailFolder;
import org.notification.domain.MessageType;
import org.notification.repository.IncomingTransactionInfoMongoRepository;
import org.notification.scheduler.jobs.IncomingMailPollingJob;
import org.notification.service.IncomingTransactionHandlerService;
import org.notification.service.MailboxPollerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * This class will be called by the Quartz job {@link IncomingMailPollingJob}
 *
 * @author dinuka
 */
@Service
@ConditionalOnExpression("!'${spring.profiles.active}'.equals('dev') && !'${spring.profiles" +
    ".active}'.equals('test')")
public class MailboxPollerServiceImpl implements MailboxPollerService {

  private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

  @Autowired
  private IncomingTransactionInfoMongoRepository incomingTransInfoRepo;

  @Autowired
  @Qualifier(MSNotificationConstants.INCOMING_MAIL_PROCESSOR_IMPL_QUALIFIER)
  private IncomingTransactionHandlerService mailHandlerService;

  @Value("${emailConfig.incoming.mail.host}")
  private String host;
  @Value("${emailConfig.incoming.mail.userName}")
  private String userName;
  @Value("${emailConfig.incoming.mail.password}")
  private String password;
  @Value("${emailConfig.incoming.mail.filterDomains: #{null}}")
  private String filterDomains;

  private List<String> filterDomainList = new LinkedList<>();

  private Store store;

  @PostConstruct
  public void init() throws MessagingException {
    Properties props = System.getProperties();
    Session session = Session.getDefaultInstance(props, null);
    store = session.getStore("pop3");
    log.info("Connecting to host : {} with username : {}", host, userName);
    store.connect(host, userName, password);
    log.info("Successfully connected to host : {} with username : {}", host, userName);
    if (StringUtils.isNotEmpty(filterDomains)) {
      for (String filterDomain : filterDomains.split(",")) {
        filterDomainList.add(StringUtils.trimToNull(filterDomain));
      }
    }
  }

  @PreDestroy
  public void destroy() throws MessagingException {
    if (store != null) {
      store.close();
    }
  }

  @Override
  public void readMailbox() {
    if (store == null || !store.isConnected()) {
      try {
        init();
      } catch (MessagingException e) {
        log.error("Unable to connect to the mail server. Message received : {}", e.getMessage());
        return;
      }
    }

    List<IncomingTransactionInfo> incomingMailList = new LinkedList<>();
    try {
      Folder folder = store.getDefaultFolder().getFolder(MailFolder.INBOX.toString());
      log.info("Reading mails from mailbox : {}", MailFolder.INBOX.toString());
      folder.open(Folder.READ_WRITE);
      // Get directory
      Message messages[] = folder.getMessages();
      log.info("Unread mail count of mailbox {} : {}", MailFolder.INBOX.toString(),
          messages.length);
      for (Message message : messages) {
        String sender = ((InternetAddress) message.getReplyTo()[0]).getAddress();
        String senderDomain = StringUtils
            .trimToEmpty(sender.substring(sender.indexOf("@") + 1, sender.length()));
        // We do not need to process mails from the domain that is
        // filtered.
        if (filterDomainList.contains(senderDomain)) {
          log.info(
              "The email is sent from a domain that is filtered so ignoring mail from sender : {}",
              sender);
          message.setFlag(Flags.Flag.DELETED, true);
          continue;
        }

        String subject = message.getSubject();
        Pattern subjectPattern = Pattern.compile("(^Re:|^RE:)(.*)");
        Matcher subjectMatcher = subjectPattern.matcher(subject);
        if (subjectMatcher.matches()) {
          subject = StringUtils.trimToEmpty(subjectMatcher.group(subjectMatcher.groupCount()));
        }

        String contentType = message.getContentType();
        String content = StringUtils.EMPTY;
        Object contentObj = message.getContent();
        Date receivedDate = message.getSentDate();

        log.info("Content type for mail message is : {}", contentType);

        if (contentType == null) {
          continue;
        }
        // The content type will have multiple tags separated with ";"
        // so we need to do a regular expression query as opposed to a
        // string
        // equals.
        if (contentType.matches(".*text/html.*|.*text/plain.*")) {
          content = (String) contentObj;
        } else if (contentType.matches(".*multipart.*")) {
          MimeMultipart multiPart = ((MimeMultipart) contentObj);

          // We need to iterate through and only get either the html
          // or plain text part.
          inner:
          for (int x = 0; x < multiPart.getCount(); x++) {
            BodyPart part = multiPart.getBodyPart(x);
            if (part.getContentType() != null
                && part.getContentType().matches(".*text/html.*|.*text/plain.*")) {
              content = (String) part.getContent();
              break inner;
            }
          }
          if (StringUtil.isBlank(content)) {
            try (ByteArrayOutputStream contentOutputstream = new ByteArrayOutputStream()) {
              multiPart.writeTo(contentOutputstream);
              content = contentOutputstream.toString();
            }
            log.info("No content found for email sent by : {}", sender);
          }
        }

        log.info("Persisting the mail message to the database");
        DateTime receivedDateTime = new DateTime(receivedDate);
        IncomingTransactionInfo incomingTransInfo = new IncomingTransactionInfo(
            content, subject, sender, MessageType.EMAIL, receivedDateTime,
            Collections.list(message.getAllHeaders()));

        incomingTransInfoRepo.insert(incomingTransInfo);

        log.info("Successfully persisted the message to the database");
        incomingMailList.add(incomingTransInfo);
        // This will delete the mail from the inbox after you close the
        // folder since we already keep a track of all the received
        // emails within our collection
        message.setFlag(Flags.Flag.DELETED, true);
      }

      // Close connection
      folder.close(true);
    } catch (Exception e) {
      log.error("Exception received while polling for emails from folder : {} with exception : {}",
          MailFolder.INBOX.toString(), e.getMessage());
    } finally {
      if (!CollectionUtils.isEmpty(incomingMailList)) {
        log.info("Calling the Email handler service to process the retrieved emails.");
        mailHandlerService.process(incomingMailList);
      }
    }
  }
}
