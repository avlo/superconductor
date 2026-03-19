package com.prosilion.superconductor.config;

import com.prosilion.superconductor.base.util.NostrComprehensiveRelayService;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "false")
public class NostrWsConfig {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public NostrComprehensiveRelayService nostrRelayService(
      @Value("${superconductor.relay.url}") String relayUri,
      Duration requestTimeoutDuration) throws ExecutionException, InterruptedException {
    return new NostrComprehensiveRelayService(relayUri, requestTimeoutDuration);
  }
}
