package com.prosilion.superconductor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Streams;
import com.prosilion.subdivisions.event.EventPublisher;
import com.prosilion.subdivisions.request.RelaySubscriptionsManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.api.factory.impl.NIP01Impl.EventMessageFactory;
import nostr.base.Command;
import nostr.event.impl.GenericEvent;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EventMessage;
import nostr.event.message.OkMessage;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class NostrRelayService {
  private final EventPublisher eventPublisher;
  private final RelaySubscriptionsManager relaySubscriptionsManager;

  public NostrRelayService(@Value("${superconductor.relay.uri}") String relayUri) throws ExecutionException, InterruptedException {
    log.debug("relayUri: \n{}", relayUri);
    this.eventPublisher = new EventPublisher(relayUri);
    this.relaySubscriptionsManager = new RelaySubscriptionsManager(relayUri);
  }

  public NostrRelayService(
      @Value("${superconductor.relay.uri}") String relayUri,
      SslBundles sslBundles
  ) throws ExecutionException, InterruptedException {
    log.debug("relayUri: \n{}", relayUri);
    log.debug("sslBundles: \n{}", sslBundles);
    final SslBundle server = sslBundles.getBundle("server");
    log.debug("sslBundles name: \n{}", server);
    log.debug("sslBundles key: \n{}", server.getKey());
    log.debug("sslBundles protocol: \n{}", server.getProtocol());
    this.eventPublisher = new EventPublisher(relayUri, sslBundles);
    this.relaySubscriptionsManager = new RelaySubscriptionsManager(relayUri, sslBundles);
  }

  public void createEvent(@NonNull String eventJson) throws IOException {
    assertTrue(
        getOkMessage(
            sendEvent(eventJson)
        ).getFlag());
  }

  public void createEvent(@NonNull GenericEvent event) throws IOException {
    assertTrue(
        getOkMessage(
            sendEvent(
                new EventMessageFactory(event).create())
        ).getFlag());
  }

  private static OkMessage getOkMessage(List<String> received) {
    return Streams.findLast(received.stream())
        .map(baseMessage -> {
          try {
            return new BaseMessageDecoder<OkMessage>().decode(baseMessage);
          } catch (JsonProcessingException e) {
            return null;
          }
        })
        .orElseThrow();
  }

  private List<String> sendEvent(String eventJson) throws IOException {
    eventPublisher.createEvent(eventJson);
    log.debug("socket send event JSON content\n  {}", eventJson);
    return getEvents();
  }

  private List<String> sendEvent(EventMessage eventMessage) throws IOException {
    eventPublisher.sendEvent(eventMessage);
    log.debug("socket send EventMessage content\n  {}", eventMessage.getEvent());
    return getEvents();
  }

  public List<String> getEvents() {
    List<String> events = eventPublisher.getEvents();
    log.debug("received relay response:");
    log.debug("\n" + events.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    return events;
  }

  public List<String> sendRequestReturnEvents(@NonNull ReqMessage reqMessage) throws JsonProcessingException {
    return relaySubscriptionsManager.sendRequestReturnEvents(reqMessage);
  }
  
  public List<String> sendRequestReturnEvents(@NonNull String reqJson, @NonNull String clientUuid) {
    List<String> returnedEvents = relaySubscriptionsManager.sendRequestReturnEvents(clientUuid, reqJson);
    log.debug("-------------");
    log.debug("socket [{}] getEvents():", clientUuid);
    returnedEvents.forEach(event -> log.debug("  {}\n", event));
    log.debug("222222222222\n");
    return returnedEvents;
  }

  public Map<Command, Optional<String>> sendRequest(@NonNull String reqJson, @NonNull String clientUuid) {
    Map<Command, List<String>> resultsMap = relaySubscriptionsManager.sendRequestReturnCommandResultsMap(clientUuid, reqJson);
    List<String> returnedEvents = resultsMap.get(Command.EVENT);
    log.debug("socket [{}] getEvents():", clientUuid);
    returnedEvents.forEach(event -> log.debug("  {}\n", event));
    log.debug("222222222222\n");
    String joined = String.join(",", returnedEvents);
    log.debug("-------------");
    Map<Command, Optional<String>> returnMap = new HashMap<>();
    returnMap.put(Command.EOSE, Optional.of(resultsMap.get(Command.EOSE).getFirst()));
    Optional<String> value = Optional.of(joined).orElseThrow().isEmpty() ? Optional.empty() : Optional.of(joined);
    returnMap.put(Command.EVENT, value);
    return returnMap;
  }
}
