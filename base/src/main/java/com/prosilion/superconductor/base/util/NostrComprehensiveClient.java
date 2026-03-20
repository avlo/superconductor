package com.prosilion.superconductor.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.nostr.message.ReqMessage;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.lang.NonNull;

@Slf4j
public class NostrComprehensiveClient {
  private final com.prosilion.subdivisions.client.reactive.NostrComprehensiveClient clientSubscriber;
  Duration requestTimeoutDuration;

  public NostrComprehensiveClient(@NonNull String relayUrl, Duration requestTimeoutDuration) {
    log.debug("constructor called with relayUrl [{}]", relayUrl);
    this.requestTimeoutDuration = requestTimeoutDuration;
    this.clientSubscriber = new com.prosilion.subdivisions.client.reactive.NostrComprehensiveClient(relayUrl);
  }

  public NostrComprehensiveClient(@Value("${superconductor.relay.url}") @NonNull String relayUrl, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
    log.debug("constructor called with relay url {} and sslBundles {}", relayUrl, sslBundles);
    final SslBundle server = sslBundles.getBundle("server");
    log.debug("sslBundles name: \n{}", server);
    log.debug("sslBundles key: \n{}", server.getKey());
    log.debug("sslBundles protocol: \n{}", server.getProtocol());
    this.clientSubscriber = new com.prosilion.subdivisions.client.reactive.NostrComprehensiveClient(relayUrl, sslBundles);
  }

  public OkMessage send(@NonNull EventMessage eventMessage) throws IOException {
    return clientSubscriber.send(eventMessage);
  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage) throws JsonProcessingException, NostrException {
    return clientSubscriber.send(reqMessage, requestTimeoutDuration);
  }
}
