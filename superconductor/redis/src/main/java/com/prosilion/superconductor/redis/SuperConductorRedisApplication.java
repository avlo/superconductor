package com.prosilion.superconductor.redis;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableRedisDocumentRepositories(basePackages = "com.prosilion.superconductor.lib.redis.repository")
public class SuperConductorRedisApplication extends SpringBootServletInitializer {

  /**
   * spring-boot WAR hook
   */
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(SuperConductorRedisApplication.class);
  }

  /**
   * spring-boot executable JAR hook
   */
  public static void main(String[] args) {
    SpringApplication.run(SuperConductorRedisApplication.class, args);
  }
}
