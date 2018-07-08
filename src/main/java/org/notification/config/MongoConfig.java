package org.notification.config;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This class configures the mongo client.
 *
 * @author dinuka
 */
public class MongoConfig {

  /**
   * For now mongo will be configured without SSL.
   */
  @Configuration
  @EnableMongoRepositories(
      basePackages = {"org.notification.repository"})
  @EnableTransactionManagement
  public static class MongoConfigDev extends AbstractMongoConfiguration {


    @Autowired
    private SystemConfigProperties systemConfigProperties;

    @Override
    protected String getDatabaseName() {
      String serviceName = systemConfigProperties.getServerName();
      String environmentName = systemConfigProperties.getActiveSpringProfile();
      return serviceName + "-" + environmentName;
    }

    @Override
    public MongoClient mongoClient() {
      String mongoUri = systemConfigProperties.getMongoUri();
      String[] addresses = mongoUri.split(",");
      List<ServerAddress> servers = new ArrayList<>();
      for (String address : addresses) {
        String[] split = address.trim().split(":");
        servers.add(new ServerAddress(split[0].trim(), Integer.parseInt(split[1].trim())));
      }
      return new MongoClient(servers);
    }
  }

}

