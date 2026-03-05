package com.prosilion.superconductor.autoconfigure.base.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.reactive.ReactiveRequestConsolidator;
import com.prosilion.superconductor.base.util.RequestSubscriber;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class NostrRelayReqConsolidatorService {
  private final ReactiveRequestConsolidator requestConsolidator;
  private final Duration requestTimeoutDuration;

  public NostrRelayReqConsolidatorService(@NonNull Duration requestTimeoutDuration) {
    log.debug("constructor (using ReactiveRequestConsolidator)");
    this.requestTimeoutDuration = requestTimeoutDuration;
    this.requestConsolidator = new ReactiveRequestConsolidator();
  }

//  public NostrRelayReqService(@Value("${superconductor.relay.url}") @NonNull String relayUrl, @NonNull SslBundles sslBundles) throws ExecutionException, InterruptedException {
//    log.debug("{} constructor called with relay url {} and sslBundles {}", getClass().getSimpleName(), relayUrl, sslBundles);
//    final SslBundle server = sslBundles.getBundle("server");
//    log.debug("sslBundles name: \n{}", server);
//    log.debug("sslBundles key: \n{}", server.getKey());
//    log.debug("sslBundles protocol: \n{}", server.getProtocol());
//    this.nostrRelayService = new ReactiveNostrRelayClient(relayUrl, sslBundles);
//  }

  public List<BaseMessage> send(@NonNull ReqMessage reqMessage, @NonNull String relayUrl) throws NostrException {
    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>(requestTimeoutDuration);
    try {
      log.debug("NostrRelayReqConsolidatorService send() called with reqMessage:\n  {}\nrelayUrl:\n{}",
          Util.prettyFormatJson(reqMessage.encode()),
          relayUrl);
      log.debug("NostrRelayReqConsolidatorService send() should return items:\n");

      this.requestConsolidator.send(reqMessage, subscriber, relayUrl);
      List<BaseMessage> items = subscriber.getItems();
      log.debug("\n\n\nYYYYYYYYYYYYYYYYYYYYYYYYYYY\nYYYYYYYYYYYYYYYYYYYYYYYYYYY\n\n  {}\n\nYYYYYYYYYYYYYYYYYYYYYYYYYYY\nYYYYYYYYYYYYYYYYYYYYYYYYYYY\n\n\n",
          items.stream().map(baseMessage ->
          {
            try {
              return Util.prettyFormatJson(baseMessage.encode()) + "\n";
            } catch (JsonProcessingException e) {
              throw new RuntimeException(e);
            }
          }).collect(Collectors.joining(",")));
      subscriber.dispose();
      return items;
    } catch (JsonProcessingException e) {
      throw new NostrException(e);
    }
  }

  public void disconnect(@NonNull String subscriptionId) {
    this.requestConsolidator.removeRelay(subscriptionId);
  }
}
