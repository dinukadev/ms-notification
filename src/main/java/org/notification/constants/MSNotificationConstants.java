package org.notification.constants;

/**
 * @author dinuka
 */
public class MSNotificationConstants {

    public static final String RESPONSE_SUCCESS = "Success";

    public static final String API_VERSION_1 = "/v1";

    public static final String API_SYNC_SENDEMAIL = "/notification/sync/sendEmail";

    public static final String API_ASYNC_SENDEMAIL = "/notification/async/sendEmail";

    public static final String API_SYNC_SENDSMS = "/notification/sync/sendSMS";

    public static final String API_ASYNC_SENDSMS = "/notification/async/sendSMS";

    public static final String INCOMING_MAIL_PROCESSOR_IMPL_QUALIFIER = "incomingMailHandler";

    public static final String INCOMING_SMS_PROCESSOR_IMPL_QUALIFIER = "incomingSMSHandler";

    public static class NotificationClientConfigProperties {
        public static final String CALLBACK_REQUIRED = "callbackrequired";
    }

    public static class SchedulerConstants {
        public static final String MAIL_POLLING_TRIGGER_KEY = "mailPollerTrigger";
        public static final String MAIL_POLLING_GROUP = "mailPollerGroup";
        public static final String MAIL_POLLING_JOB_KEY = "mailPollingJob";
        public static final String QUARTZ_SCHEDULER_DB_NAME = "quartz.jobStore.dbName";
        public static final String SMS_POLLING_TRIGGER_KEY = "smsPollingTrigger";
        public static final String SMS_POLLING_GROUP = "smsPollerGroup";
    }

    public static class IncomingMailMatchingChainConstants {
        public static final String SENDER_NAME_REF_MATCHING_HANDLER = "senderNameReferenceMatchingHandler";
        public static final String REFERENCE_HEADER_MATCHING_HANDLER = "referenceHeaderMatchingHandler";
        public static final String SUBJECT_MATCHING_HANDLER = "subjectMatchingHandler";
    }
}
