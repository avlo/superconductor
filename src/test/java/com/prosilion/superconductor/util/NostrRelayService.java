package com.prosilion.superconductor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Streams;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nostr.api.factory.impl.NIP01Impl.EventMessageFactory;
import nostr.base.Command;
import nostr.event.impl.GenericEvent;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EoseMessage;
import nostr.event.message.EventMessage;
import nostr.event.message.OkMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class NostrRelayService {
  private final StandardWebSocketClient eventSocketClient;
  private Map<String, StandardWebSocketClient> requestSocketClientMap = new ConcurrentHashMap<>();
  private final String relayUri;
  private SslBundles sslBundles;

  public NostrRelayService(@Value("${superconductor.relay.uri}") String relayUri) throws ExecutionException, InterruptedException {
    this.relayUri = relayUri;
    log.debug("relayUri: \n{}", relayUri);
    this.eventSocketClient = new StandardWebSocketClient(relayUri);
  }

  public NostrRelayService(
      @Value("${superconductor.relay.uri}") String relayUri,
      SslBundles sslBundles
  ) throws ExecutionException, InterruptedException {
    this.relayUri = relayUri;
    log.debug("relayUri: \n{}", relayUri);
    this.sslBundles = sslBundles;
    log.debug("sslBundles: \n{}", sslBundles);
    final SslBundle server = sslBundles.getBundle("server");
    log.debug("sslBundles name: \n{}", server);
    log.debug("sslBundles key: \n{}", server.getKey());
    log.debug("sslBundles protocol: \n{}", server.getProtocol());
    this.eventSocketClient = new StandardWebSocketClient(relayUri, sslBundles);
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
    eventSocketClient.send(eventJson);
    log.debug("socket send event JSON content\n  {}", eventJson);
    return getEvents();
  }

  private List<String> sendEvent(EventMessage eventMessage) throws IOException {
    eventSocketClient.send(eventMessage);
    log.debug("socket send EventMessage content\n  {}", eventMessage.getEvent());
    return getEvents();
  }

  public List<String> getEvents() {
    List<String> events = eventSocketClient.getEvents();
    log.debug("received relay response:");
    log.debug("\n" + events.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    return events;
  }

  public Map<Command, Optional<String>> sendRequest(@NonNull String reqJson, @NonNull String clientUuid) throws IOException, ExecutionException, InterruptedException {
    return sendNostrRequest(
        reqJson,
        clientUuid,
        GenericEvent.class
    );
  }

  private Map<Command, Optional<String>> sendNostrRequest(
      @NonNull String reqJson,
      @NonNull String clientUuid,
      @NonNull Class<GenericEvent> type) throws IOException, ExecutionException, InterruptedException {
    List<String> returnedEvents = request(reqJson, clientUuid);

    log.debug("55555555555555555");
    log.debug("after REQUEST:");
    log.debug("key:\n  [{}]\n", clientUuid);
    log.debug("-----------------");
    log.debug("returnedEvents:");
    log.debug(returnedEvents.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    log.debug("55555555555555555");

    Optional<String> eoseMessageOptional = returnedEvents.stream().map(baseMessage -> {
          try {
            return new BaseMessageDecoder<>().decode(baseMessage);
          } catch (JsonProcessingException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findFirst()
        .map(EoseMessage::getSubscriptionId);

    Optional<String> eventMessageOptional = returnedEvents.stream().map(baseMessage -> {
          try {
            return new BaseMessageDecoder<>().decode(baseMessage);
          } catch (JsonProcessingException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(eventMessage -> (GenericEvent) eventMessage.getEvent())
        .sorted(Comparator.comparing(GenericEvent::getCreatedAt))
        .map(event -> new BaseEventEncoder<>(event).encode())
        .reduce((first, second) -> second); // gets last/aka, most recently dated event

    Map<Command, Optional<String>> returnMap = new HashMap<>();
    returnMap.put(Command.EOSE, eoseMessageOptional);
    returnMap.put(Command.EVENT, eventMessageOptional);
    return returnMap;
  }

  private List<String> request(@NonNull String reqJson, @NonNull String clientUuid) throws ExecutionException, InterruptedException, IOException {
//    final String subscriberPrefixEventIdSuffix = subscriberIdPrefix + clientUuid;
    final String subscriberPrefixEventIdSuffix = clientUuid;
    final StandardWebSocketClient existingSubscriberUuidWebClient = requestSocketClientMap.get(subscriberPrefixEventIdSuffix);
    if (existingSubscriberUuidWebClient != null) {
      log.debug("3333333333333 existing REQ socket\nkey:\n  [{}]\nsocket:\n  [{}]\n\n", subscriberPrefixEventIdSuffix, existingSubscriberUuidWebClient.getClientSession().getId());
      List<String> events = existingSubscriberUuidWebClient.getEvents();
      log.debug("-------------");
      log.debug("socket getEvents():");
      events.forEach(event -> log.debug("  {}\n", event));
      log.debug("33333333333\n");
      return events;
    }

    requestSocketClientMap.put(subscriberPrefixEventIdSuffix, getStandardWebSocketClient());

    final StandardWebSocketClient newSubscriberUuidWebClient = requestSocketClientMap.get(subscriberPrefixEventIdSuffix);
    final String newSubscriberUuidWebClientsessionId = newSubscriberUuidWebClient.getClientSession().getId();
    log.debug("222222222222 new REQ socket\nkey:\n  [{}]\nsocket:\n  [{}]\n\n", subscriberPrefixEventIdSuffix, newSubscriberUuidWebClientsessionId);
    newSubscriberUuidWebClient.send(reqJson);
    List<String> events = newSubscriberUuidWebClient.getEvents();
    log.debug("-------------");
    log.debug("socket [{}] getEvents():", newSubscriberUuidWebClientsessionId);
    events.forEach(event -> log.debug("  {}\n", event));
    log.debug("222222222222\n");
    return events;
  }

  private StandardWebSocketClient getStandardWebSocketClient() throws ExecutionException, InterruptedException {
    return Objects.nonNull(sslBundles) ? new StandardWebSocketClient(relayUri, sslBundles) :
        new StandardWebSocketClient(relayUri);
  }
}
