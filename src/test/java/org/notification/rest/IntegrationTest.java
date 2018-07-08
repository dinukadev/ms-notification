package org.notification.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.notification.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;

/**
 * @author dinuka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
@ActiveProfiles("test")
public abstract class IntegrationTest {

  @Autowired
  protected WebApplicationContext webApplicationContext;

  protected MockMvc mockMvc;

  protected ObjectMapper mapper;

  @Autowired
  protected MongoTemplate mongoTemplate;

  protected List<Class> collectionsToBeCleared;

  @Value("${quartz.jobStore.dbName}")
  private String quartzMongoDbName;

  @BeforeClass
  public static void init() {
    System.setProperty("log.file.name", "ms-notification");
  }

  @Before
  public void setUp() {
    mapper = new ObjectMapper();
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @After
  public void clearData() {
    if (collectionsToBeCleared != null) {
      for (Class collectionClass : collectionsToBeCleared) {
        mongoTemplate.dropCollection(collectionClass);
      }
    }
  }

  @Configuration
  public static class MockJavaMailSender {

    @Bean
    public JavaMailSender javaMailSender() {
      return mock(JavaMailSender.class);
    }
  }


  /**
   * This is used so that we can test the async functionality in a synchronous manner.
   *
   * @author dinuka
   */
  @Configuration
  public static class MockSyncTaskExecutor {

    @Bean
    @Primary
    public Executor syncExecutor() {
      return new SyncTaskExecutor();
    }
  }

  @Configuration
  public static class MockRestTemplate {

    @Bean
    public RestTemplate restTemplate() {
      return mock(RestTemplate.class);
    }
  }
}
