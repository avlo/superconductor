package com.prosilion.superconductor.config;

import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.File;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EmbeddedRedisStandalone
public class SingleContainerCuratedTestConfig {
  public static final String SUPERCONDUCTOR_APP_TWO = "superconductor-app-two";
  public static final String SUPERCONDUCTOR_APP_THREE = "superconductor-app-three";

  @Bean
  @ServiceConnection
  public ComposeContainer composeSingleContainerSuperconductorDocker() {
    return new ComposeContainer(
       new File("src/test/resources/superconductor-docker-compose-single-remote-dev/superconductor-docker-compose-dev-test-ws.yml"))
       .waitingFor("superconductor-db-two", Wait.forHealthcheck())
       .waitingFor(SUPERCONDUCTOR_APP_TWO, Wait.forLogMessage(".*Started SuperConductorRedisApplication.*\\n", 1))
       .withExposedService(SUPERCONDUCTOR_APP_TWO, 5555)

       .waitingFor("superconductor-db-three", Wait.forHealthcheck())
       .waitingFor(SUPERCONDUCTOR_APP_THREE, Wait.forLogMessage(".*Started SuperConductorRedisApplication.*\\n", 1))
       .withExposedService(SUPERCONDUCTOR_APP_THREE, 5555)
       .withRemoveVolumes(true);
  }
}
