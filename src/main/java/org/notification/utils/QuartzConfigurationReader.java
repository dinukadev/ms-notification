package org.notification.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.TypeToken;
import org.notification.scheduler.jobstore.EnvironmentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QuartzConfigurationReader {

  private static final Logger log = LoggerFactory.getLogger(QuartzConfigurationReader.class);

  private final static String YAML_BASE_ENTRY = "environments";

  public static Map<String, EnvironmentConfiguration> readConfiguration(String fileName) {
    Map<String, EnvironmentConfiguration> configMap = new HashMap<>();
    try {
      List<EnvironmentConfiguration> environmentConfigurations =
          (List<EnvironmentConfiguration>) (List<?>) YamlParser
              .parseYamlFile(YAML_BASE_ENTRY, fileName,
                  new TypeToken<List<EnvironmentConfiguration>>() {
                  }.getType());
      for (EnvironmentConfiguration environmentConfiguration : environmentConfigurations) {
        configMap.put(environmentConfiguration.getName(), environmentConfiguration);
      }
    } catch (IOException e) {
      log.error("Unable to read configuration file [{}]: {}", fileName, e.getMessage(), e);
    }
    return configMap;
  }
}
