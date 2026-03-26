package com.prosilion.superconductor.config;

import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
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

//  @Bean
//  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//  public NostrComprehensiveClient nostrRelayService(
//      @Value("${superconductor.relay.url}") String relayUri) throws ExecutionException, InterruptedException {
//    return new NostrComprehensiveClient(relayUri);
//  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public NostrEventPublisher nostrEventPublisher(@Value("${superconductor.relay.url}") String relayUrl) {
    return new NostrEventPublisher(relayUrl);
  }
}
