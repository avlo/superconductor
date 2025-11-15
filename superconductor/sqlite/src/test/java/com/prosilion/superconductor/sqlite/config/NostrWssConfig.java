package com.prosilion.superconductor.sqlite.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@ConditionalOnProperty(
    name = "server.ssl.enabled",
    havingValue = "true")
public class NostrWssConfig {

//  @Bean
//  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//  public NostrRelayService nostrRelayService(
//      @NonNull @Value("${superconductor.relay.url}") String relayUri,
//      @NonNull SslBundles sslBundles
//  ) throws ExecutionException, InterruptedException {
//    return new NostrRelayService(relayUri, sslBundles);
//  }
}
