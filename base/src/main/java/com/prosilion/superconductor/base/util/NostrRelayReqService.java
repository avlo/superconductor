package com.prosilion.superconductor.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.subdivisions.client.reactive.ReactiveRequestConsolidator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class NostrRelayReqService {
  private final ReactiveRequestConsolidator requestConsolidator;

  public NostrRelayReqService() {
    log.debug("{} constructor (using ReactiveRequestConsolidator)", getClass().getSimpleName());
    this.requestConsolidator = new ReactiveRequestConsolidator();
  }

//  public NostrRelayService(@NonNull String relayUrl, boolean on) {
//    this(relayUrl);
//    if (on) log.debug("{} constructor called with relayUrl [{}]", getClass().getSimpleName(), relayUrl);
//  }

//  public NostrRelayReqService(@Value("${superconductor.relay.url}") @NonNull String relayUrl, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
//    log.debug("{} constructor called with relay url {} and sslBundles {}", getClass().getSimpleName(), relayUrl, sslBundles);
//    final SslBundle server = sslBundles.getBundle("server");
//    log.debug("sslBundles name: \n{}", server);
//    log.debug("sslBundles key: \n{}", server.getKey());
//    log.debug("sslBundles protocol: \n{}", server.getProtocol());
//    this.nostrRelayService = new ReactiveNostrRelayClient(relayUrl, sslBundles);
//  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage, @NonNull String relayUrl) throws JsonProcessingException, NostrException {
    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>();
    this.requestConsolidator.addRelay(reqMessage.getSubscriptionId(), relayUrl);
    this.requestConsolidator.send(reqMessage, subscriber);
    return subscriber.getItems();
  }

  public void disconnect(@NonNull String subscriptionId) {
    this.requestConsolidator.removeRelay(subscriptionId);
  }
}
