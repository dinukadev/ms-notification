package org.notification.scheduler.jobstore;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.novemberain.quartz.mongodb.MongoDBJobStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.notification.constants.MSNotificationConstants;
import org.notification.constants.SystemProperties;
import org.notification.utils.QuartzConfigurationReader;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/**
 * <p>
 * We extend the {@link MongoDBJobStore} because we need to set the custom mongo db parameters. Some
 * of the configuration comes from system properties set via docker and the others come via the
 * application.yml files we have for each environment.
 * </p>
 * <p>
 * < These are set as part of initialization. This class is initialized by {@link
 * StdSchedulerFactory} and defined in the quartz.properties file.
 * <p>
 * </p>
 *
 * @author dinuka
 */
public class NotificationQuartzJobScheduler extends MongoDBJobStore {

  private static String mongoAddresses;
  private static String userName;
  private static String password;
  private static String dbName;
  private static MongoClient mongoClient;

  static {
    initializeMongo();
  }

  public NotificationQuartzJobScheduler() {
    super(mongoClient);
    setDbName(dbName);
  }

  private static void initializeMongo() {
    String env = System.getProperty(SystemProperties.ENVIRONMENT);
    env = StringUtils.isNotBlank(env) ? env : "dev";

    YamlPropertiesFactoryBean commonProperties = new YamlPropertiesFactoryBean();
    commonProperties.setResources(new ClassPathResource("application.yml"));

    userName = StringUtils.trimToEmpty(commonProperties.getObject()
        .getProperty(SystemProperties.SERVER_NAME));
    password = StringUtils.trimToEmpty(System.getenv("DB_PASSWORD"));
    dbName = commonProperties.getObject().getProperty(
        MSNotificationConstants.SchedulerConstants.QUARTZ_SCHEDULER_DB_NAME);
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();

    userName = commonProperties.getObject().getProperty(SystemProperties.SERVER_NAME);

    String envConfigFileName = StringUtils.trimToEmpty(commonProperties.getObject()
        .getProperty(SystemProperties.QUARTZ_ENV_CONFIG));
    Map<String, EnvironmentConfiguration> environmentConfigurations =
        QuartzConfigurationReader.readConfiguration(envConfigFileName);
    if (environmentConfigurations != null) {
      EnvironmentConfiguration environmentConfiguration = environmentConfigurations.get(env);
      if (environmentConfiguration != null) {
        yaml.setResources(new ClassPathResource(environmentConfiguration.getConfig()));
        mongoAddresses = yaml.getObject().getProperty(SystemProperties.MONGO_URI);
        if (environmentConfiguration.isSsl()) {
          mongoClient = getMongoClient(userName, password, dbName, mongoAddresses);
        } else {
          mongoClient = getMongoClientWithoutSSL(mongoAddresses);
        }
      }
    }

  }

  private static MongoClient getMongoClient(String userName, String password,
                                            String db, String mongoURI) {
    MongoCredential credential = MongoCredential.createScramSha1Credential(
        userName, db, password.toCharArray());

    String[] addresses = mongoURI.split(",");
    List<ServerAddress> servers = new ArrayList<>();
    for (String address : addresses) {
      String[] split = address.trim().split(":");
      servers.add(new ServerAddress(split[0].trim(),
          Integer.parseInt(split[1].trim())));
    }

    return new MongoClient(servers, Arrays.asList(credential),
        MongoClientOptions.builder().sslEnabled(true)
            .sslInvalidHostNameAllowed(true).build());
  }

  private static MongoClient getMongoClientWithoutSSL(String mongoURI) {
    String[] addresses = mongoURI.split(",");
    List<ServerAddress> servers = new ArrayList<>();
    for (String address : addresses) {
      String[] split = address.trim().split(":");
      servers.add(new ServerAddress(split[0].trim(),
          Integer.parseInt(split[1].trim())));
    }
    return new MongoClient(servers);
  }

}
