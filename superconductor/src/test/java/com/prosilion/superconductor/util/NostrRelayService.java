package com.prosilion.superconductor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.subdivisions.client.reactive.ReactiveNostrRelayClient;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseMessage;
import nostr.event.message.EventMessage;
import nostr.event.message.OkMessage;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

@Slf4j
public class NostrRelayService {
  private final ReactiveNostrRelayClient<BaseMessage> nostrRelayService;

  public NostrRelayService(@Value("${superconductor.relay.url}") @NonNull String relayUri) {
    log.debug("relayUri: \n{}", relayUri);
    this.nostrRelayService = new ReactiveNostrRelayClient<>(relayUri);
  }

  public NostrRelayService(@Value("${superconductor.relay.url}") @NonNull String relayUri, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
    log.debug("relayUri: \n{}", relayUri);
    log.debug("sslBundles: \n{}", sslBundles);
    final SslBundle server = sslBundles.getBundle("server");
    log.debug("sslBundles name: \n{}", server);
    log.debug("sslBundles key: \n{}", server.getKey());
    log.debug("sslBundles protocol: \n{}", server.getProtocol());
    this.nostrRelayService = new ReactiveNostrRelayClient<>(relayUri, sslBundles);
  }

  public OkMessage send(@NonNull EventMessage eventMessage) throws IOException {
    TestSubscriber<OkMessage> subscriber = new TestSubscriber<>();
    nostrRelayService.send(eventMessage, subscriber);
    return subscriber.getItems().getFirst();
  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage) throws JsonProcessingException {
    TestSubscriber<BaseMessage> subscriber = new TestSubscriber<>();
    nostrRelayService.send(reqMessage, subscriber);
    return subscriber.getItems();
  }
}
