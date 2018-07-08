package org.notification.config;

import javax.annotation.PostConstruct;
import org.notification.constants.MSNotificationConstants.SchedulerConstants;
import org.notification.scheduler.jobs.IncomingMailPollingJob;
import org.notification.scheduler.jobs.IncomingSMSPollingJob;
import org.joda.time.DateTime;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * This will configure the job to run within quartz.
 *
 * @author dinuka
 */
@Configuration
public class JobConfiguration {

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  private static JobDetail mailPollingJob() {
    JobDetailImpl jobDetail = new JobDetailImpl();
    jobDetail.setKey(
        new JobKey(SchedulerConstants.MAIL_POLLING_JOB_KEY, SchedulerConstants.MAIL_POLLING_GROUP));
    jobDetail.setJobClass(IncomingMailPollingJob.class);
    jobDetail.setDurability(true);
    return jobDetail;
  }

  private static JobDetail smsPollingJob() {
    JobDetailImpl jobDetail = new JobDetailImpl();
    jobDetail.setKey(new JobKey(SchedulerConstants.SMS_POLLING_TRIGGER_KEY,
        SchedulerConstants.SMS_POLLING_GROUP));
    jobDetail.setJobClass(IncomingSMSPollingJob.class);
    jobDetail.setDurability(true);
    return jobDetail;
  }

  private static Trigger mailPollingSchedulerTrigger() {
    return newTrigger().forJob(mailPollingJob())
        .withIdentity(SchedulerConstants.MAIL_POLLING_TRIGGER_KEY,
            SchedulerConstants.MAIL_POLLING_GROUP)
        .withPriority(50).withSchedule(SimpleScheduleBuilder.repeatMinutelyForever())
        .startAt(DateTime.now().plusSeconds(3).toDate()).build();
  }

  private static Trigger smsPollingSchedulerTrigger() {
    return newTrigger().forJob(smsPollingJob())
        .withIdentity(SchedulerConstants.SMS_POLLING_TRIGGER_KEY,
            SchedulerConstants.SMS_POLLING_GROUP)
        .withPriority(50).withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(30))
        .startAt(DateTime.now().plusSeconds(3).toDate()).build();
  }

  @PostConstruct
  private void initialize() throws Exception {
    schedulerFactoryBean.getScheduler().addJob(mailPollingJob(), true, true);
    if (!schedulerFactoryBean.getScheduler().checkExists(
        new TriggerKey(SchedulerConstants.MAIL_POLLING_TRIGGER_KEY,
            SchedulerConstants.MAIL_POLLING_GROUP))) {
      schedulerFactoryBean.getScheduler().scheduleJob(mailPollingSchedulerTrigger());
    }

    schedulerFactoryBean.getScheduler().addJob(smsPollingJob(), true, true);
    if (!schedulerFactoryBean.getScheduler().checkExists(
        new TriggerKey(SchedulerConstants.SMS_POLLING_TRIGGER_KEY,
            SchedulerConstants.SMS_POLLING_GROUP))) {
      schedulerFactoryBean.getScheduler().scheduleJob(smsPollingSchedulerTrigger());
    }

  }

}