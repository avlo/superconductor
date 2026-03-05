package com.prosilion.superconductor.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.subdivisions.client.reactive.ReactiveRelaySubscriptionsManager;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class SingleReqSubscriptionManager {
  private final ReactiveRelaySubscriptionsManager reactiveRelaySubscriptionsManager;
  private final Duration timeout;

  public SingleReqSubscriptionManager(
      String relayUrl, Duration duration) {
    log.debug("constructor (using ReactiveRelaySubscriptionsManager)");
    this.reactiveRelaySubscriptionsManager = new ReactiveRelaySubscriptionsManager(relayUrl);
    this.timeout = duration;
  }

//  public NostrRelayReqService(@Value("${superconductor.relay.url}") @NonNull String relayUrl, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
//    log.debug("{} constructor called with relay url {} and sslBundles {}", getClass().getSimpleName(), relayUrl, sslBundles);
//    final SslBundle server = sslBundles.getBundle("server");
//    log.debug("sslBundles name: \n{}", server);
//    log.debug("sslBundles key: \n{}", server.getKey());
//    log.debug("sslBundles protocol: \n{}", server.getProtocol());
//    this.nostrRelayService = new ReactiveNostrRelayClient(relayUrl, sslBundles);
//  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage) throws JsonProcessingException, NostrException {
    return send(reqMessage, timeout);
  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage, @NonNull Duration duration) throws JsonProcessingException, NostrException {
    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>(duration);
    this.reactiveRelaySubscriptionsManager.send(reqMessage, subscriber);
    List<BaseMessage> items = subscriber.getItems();
    return items;
  }

  public void disconnect(@NonNull String subscriptionId) {
    this.reactiveRelaySubscriptionsManager.closeSession(subscriptionId);
  }
}
