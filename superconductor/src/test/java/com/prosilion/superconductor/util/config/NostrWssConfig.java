package com.prosilion.superconductor.util.config;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ExecutionException;

@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "true")
public class NostrWssConfig {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public NostrRelayService nostrRelayService(
      @NonNull @Value("${superconductor.relay.url}") String relayUri,
      @NonNull SslBundles sslBundles
  ) throws ExecutionException, InterruptedException {
    return new NostrRelayService(relayUri, sslBundles);
  }
}
