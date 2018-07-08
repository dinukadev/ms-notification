package org.notification.scheduler.jobstore;

import java.io.Serializable;

/**
 * @author dinuka
 */
public class EnvironmentConfiguration implements Serializable {

  private static final long serialVersionUID = 5227858103483215462L;

  private String name;

  private String config;

  private boolean ssl;

  public EnvironmentConfiguration(String name, String config, boolean ssl) {
    this.name = name;
    this.config = config;
    this.ssl = ssl;
  }

  public String getName() {
    return name;
  }

  public String getConfig() {
    return config;
  }

  public boolean isSsl() {
    return ssl;
  }
}
