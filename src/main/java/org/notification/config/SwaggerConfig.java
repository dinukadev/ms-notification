package org.notification.config;

import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    ApiInfo apiInfo = new ApiInfo(
        "Notification API",
        "This micro-serivce allows you to send and receive emails and sms",
        "Version 1",
        "http://ms-notification",
        new Contact("Dinuka Arseculeratne", "https://github.com/dinukadev", "dinuka"
            + ".arseculeratne@gmail.com"),
        "License of API",
        "API license URL",
        new ArrayList<VendorExtension>());


    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.notification.controllers"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo);
  }
}
