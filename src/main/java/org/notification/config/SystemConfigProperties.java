package org.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class contains the configuration properties across the micro-service.
 *
 * @author dinuka
 */
@Component
public class SystemConfigProperties {

  @Value("${spring.profiles.active}")
  private String activeSpringProfile;

  @Value("${server.name}")
  private String serverName;

  @Value("${server.port}")
  private String serverPort;

  @Value("${spring.data.mongodb.uri}")
  private String mongoUri;

  @Value("${db.password:#{NULL}}")
  private String dbPassword;

  @Value("${LOG_FILE_NAME:#{NULL}}")
  private String logFileName;

  public String getActiveSpringProfile() {
    return activeSpringProfile;
  }

  public String getServerName() {
    return serverName;
  }

  public String getServerPort() {
    return serverPort;
  }

  public String getMongoUri() {
    return mongoUri;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public String getLogFileName() {
    return logFileName;
  }

}
