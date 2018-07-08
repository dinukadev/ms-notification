package org.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * This class will configure and setup quartz using the
 * {@link SchedulerFactoryBean}
 *
 * @author dinuka
 *
 */
@Configuration
public class QuartzConfiguration {

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
		scheduler.setApplicationContextSchedulerContextKey("applicationContext");
		scheduler.setConfigLocation(new ClassPathResource("quartz.properties"));
		scheduler.setWaitForJobsToCompleteOnShutdown(true);
		return scheduler;
	}
}
