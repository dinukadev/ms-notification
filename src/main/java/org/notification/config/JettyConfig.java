package org.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class will configure the jetty server.
 *
 * @author dinuka
 */
@Configuration
public class JettyConfig {

  @Value("${server.port}")
  private String serverPort;


  /**
   * Configuration for servlet container.
   *
   * @return configured servlet container
   */
  @Bean
  public ServletWebServerFactory servletContainer() {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    int port = Integer.parseInt(serverPort);
    factory.setPort(port);
    return factory;
  }
}

