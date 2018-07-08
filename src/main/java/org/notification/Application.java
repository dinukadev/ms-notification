package org.notification;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.notification.constants.SystemProperties;
import org.notification.service.NotificationAdapterFactory;
import org.notification.utils.ApplicationStartupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author dinuka
 */
@SpringBootApplication(scanBasePackages = "org.notification")
@EnableAsync
public class Application {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  @Autowired
  private ApplicationStartupUtil appStartupUtil;

  @Autowired
  private ClientConfiguration clientConfig;

  public static void main(String[] args) throws UnknownHostException {
    String hostAddress = InetAddress.getLocalHost().getHostAddress();
    String logFileName = System.getenv(SystemProperties.LOG_FILE_NAME);
    System.setProperty("log.file.name", logFileName + "-" + hostAddress);
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  public void init() {
    appStartupUtil.persistClientConfiguration(clientConfig.getClientConfig());
  }

  @Bean
  public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
    ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
    factoryBean.setServiceLocatorInterface(NotificationAdapterFactory.class);
    return factoryBean;
  }

  @Bean
  public Executor getAsyncExecutor() {
    return Executors.newCachedThreadPool();
  }

  @Bean
  @ConfigurationProperties
  public ClientConfiguration clientConfig() {
    return new ClientConfiguration();
  }

  /**
   * <p>
   * What this class does is, it will map all the properties defined under the "clientConfig" within
   * the application.yml file into a map which we will then use to persist to our
   * "notificationClientConfig" collection.
   * </p>
   * <p>
   * <p>
   * We do this using the {@link ConfigurationProperties} functionality of Spring which you can see
   * we have wired up in {@link Application#clientConfig()}
   * <p>
   * </p>
   *
   * @author dinuka
   */
  public static class ClientConfiguration {
    private Map<String, String> clientConfig = new HashMap<>();

    public Map<String, String> getClientConfig() {
      return clientConfig;
    }
  }
}
