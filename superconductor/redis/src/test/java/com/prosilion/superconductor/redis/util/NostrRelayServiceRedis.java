package com.prosilion.superconductor.redis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.subdivisions.client.reactive.ReactiveNostrRelayClient;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.lang.NonNull;

@Slf4j
public class NostrRelayServiceRedis {
  private final ReactiveNostrRelayClient nostrRelayService;

  public NostrRelayServiceRedis(@Value("${superconductor.relay.url}") @NonNull String relayUri) {
    log.debug("relayUri: \n{}", relayUri);
    this.nostrRelayService = new ReactiveNostrRelayClient(relayUri);
  }

  public NostrRelayServiceRedis(@Value("${superconductor.relay.url}") @NonNull String relayUri, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
    log.debug("relayUri: \n{}", relayUri);
    log.debug("sslBundles: \n{}", sslBundles);
    final SslBundle server = sslBundles.getBundle("server");
    log.debug("sslBundles name: \n{}", server);
    log.debug("sslBundles key: \n{}", server.getKey());
    log.debug("sslBundles protocol: \n{}", server.getProtocol());
    this.nostrRelayService = new ReactiveNostrRelayClient(relayUri, sslBundles);
  }

  public OkMessage send(@NonNull EventMessage eventMessage) throws IOException {
    TestSubscriberRedis<OkMessage> subscriber = new TestSubscriberRedis<>();
    nostrRelayService.send(eventMessage, subscriber);
    return subscriber.getItems().getFirst();
  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage) throws JsonProcessingException, NostrException {
    TestSubscriberRedis<BaseMessage> subscriber = new TestSubscriberRedis<>();
    nostrRelayService.send(reqMessage, subscriber);
    return subscriber.getItems();
  }
}
