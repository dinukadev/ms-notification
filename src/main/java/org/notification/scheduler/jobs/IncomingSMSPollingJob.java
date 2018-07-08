package org.notification.scheduler.jobs;

import org.notification.service.SMSGatewayPollerService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;

import org.notification.service.SMSGatewayPollerService;

/**
 * 
 * @author dinuka
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class IncomingSMSPollingJob extends QuartzJobBean {

	private static Logger log = LoggerFactory.getLogger(IncomingMailPollingJob.class);

	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("SMS polling job executed by {}", applicationContext.getBean(Environment.class));
		applicationContext.getBean(SMSGatewayPollerService.class).readMessages();

	}

}
