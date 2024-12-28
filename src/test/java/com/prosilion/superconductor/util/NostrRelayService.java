package com.prosilion.superconductor.util;

import com.google.common.collect.Streams;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.api.factory.impl.NIP01Impl.EventMessageFactory;
import nostr.event.BaseMessage;
import nostr.event.impl.GenericEvent;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EventMessage;
import nostr.event.message.OkMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Lazy
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NostrRelayService {
  private final StandardWebSocketClient eventSocketClient;
  private Map<String, StandardWebSocketClient> requestSocketClientMap = new ConcurrentHashMap<>();
  private final String relayUri;
  private final String subscriberIdPrefix;
//  private final SslBundles sslBundles;

  public NostrRelayService(
      @Value("${superconductor.relay.url}") String relayUri,
      @Value("${superconductor.test.subscriberid.prefix}") String subscriberIdPrefix
//      , SslBundles sslBundles
  ) throws ExecutionException, InterruptedException {
    this.relayUri = relayUri;
    this.subscriberIdPrefix = subscriberIdPrefix;
//    this.sslBundles = sslBundles;
    this.eventSocketClient = new StandardWebSocketClient(relayUri
//        , sslBundles
    );
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
        .map(baseMessage -> new BaseMessageDecoder<OkMessage>().decode(baseMessage))
        .orElseThrow();
  }

  private List<String> sendEvent(String eventJson) throws IOException {
    eventSocketClient.send(eventJson);
    log.info("socket send event JSON content\n  {}", eventJson);
    return getEvents();
  }

  private List<String> sendEvent(EventMessage eventMessage) throws IOException {
    eventSocketClient.send(eventMessage);
    log.info("socket send EventMessage content\n  {}", eventMessage.getEvent());
    return getEvents();
  }

  public List<String> getEvents() {
    List<String> events = eventSocketClient.getEvents();
    log.debug("received relay response:");
    log.debug("\n" + events.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    return events;
  }

  public String sendRequest(@NonNull String eventId) throws IOException, ExecutionException, InterruptedException {
    return sendNostrRequest(
        eventId,
        GenericEvent.class
    );
  }

  private String sendNostrRequest(
      @NonNull String eventId,
      @NonNull Class<GenericEvent> type) throws IOException, ExecutionException, InterruptedException {
    List<String> returnedEvents = request(eventId);

    log.debug("55555555555555555");
    log.debug("after REQUEST:");
    log.debug("key:\n  [{}]\n", eventId);
    log.debug("-----------------");
    log.debug("returnedEvents:");
    log.debug(returnedEvents.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    log.debug("55555555555555555");
    Stream<BaseMessage> baseMessageStream = returnedEvents.stream().map(baseMessage -> new BaseMessageDecoder<>().decode(baseMessage));

    Stream<BaseMessage> baseMessageStream1 = baseMessageStream.filter(baseMessage -> !baseMessage.getCommand().equalsIgnoreCase("EOSE"));

    Stream<BaseMessage> baseMessageStream2 = baseMessageStream1.filter(EventMessage.class::isInstance);

    Stream<EventMessage> eventMessageStream = baseMessageStream2.map(EventMessage.class::cast);

    Stream<GenericEvent> genericEventStream = eventMessageStream.map(eventMessage -> (GenericEvent) eventMessage.getEvent());

    Stream<String> stringStream = genericEventStream.map(event -> new BaseEventEncoder<>(event).encode());

//    Stream<T> tStream = stringStream.map(encode -> new GenericEventDecoder<>(type).decode(encode));

//    Optional<T> max = tStream.max((a, b) -> {
//      log.debug("a: " + a);
//      log.debug("b: " + b);
//      return getCompare(a, b);
//    });
//    T latestDatedEvent = max.orElseThrow();

    List<String> list = stringStream.toList();
    String last = list.getLast();
    String latestDatedEvent = last;

//    log.debug("2222222222222222222");
//    log.debug("2222222222222222222");
//    log.debug(latestDatedEvent);
//    log.debug("2222222222222222222");
//    log.debug("2222222222222222222");
    return latestDatedEvent;
  }

  private List<String> request(@NonNull String keyWhichIsTheEventId) throws ExecutionException, InterruptedException, IOException {
    String subscriberPrefixEventIdSuffix = subscriberIdPrefix + keyWhichIsTheEventId;
    final StandardWebSocketClient webSocketClientIF = requestSocketClientMap.get(subscriberPrefixEventIdSuffix);
    if (webSocketClientIF != null) {
      log.debug("3333333333333 existing REQ socket\nkey:\n  [{}]\nsocket:\n  [{}]\n\n", subscriberPrefixEventIdSuffix, webSocketClientIF.getClientSession().getId());
      List<String> events = webSocketClientIF.getEvents();
      log.debug("-------------");
      log.debug("socket getEvents():");
      events.forEach(event -> log.debug("  {}\n", event));
      log.debug("33333333333\n");
      return events;
    }

    requestSocketClientMap.put(subscriberPrefixEventIdSuffix, new StandardWebSocketClient(relayUri
//        , sslBundles
    ));

    final StandardWebSocketClient newWebSocketClientIF = requestSocketClientMap.get(subscriberPrefixEventIdSuffix);
    log.debug("222222222222 new REQ socket\nkey:\n  [{}]\nsocket:\n  [{}]\n\n", subscriberPrefixEventIdSuffix, newWebSocketClientIF.getClientSession().getId());
    newWebSocketClientIF.send(createReqJson(subscriberPrefixEventIdSuffix, keyWhichIsTheEventId));
    List<String> events = newWebSocketClientIF.getEvents();
    log.debug("-------------");
    log.debug("socket getEvents():");
    events.forEach(event -> log.debug("  {}\n", event));
    log.debug("222222222222\n");
    return newWebSocketClientIF.getEvents();
  }

  private String createReqJson(@NonNull String subscriberId, @NonNull String dTag) {
//    TODO: re-introduce below after investigation
//    String result = ids.stream()
//        .map(s -> "\"" + s + "\"")
//        .collect(Collectors.joining(", "));
//    String joinedString = "[\"REQ\",\"" + subscriberId + "\",{\"ids\":[" + result + "]}]";
    return "[\"REQ\",\"" + subscriberId + "\",{\"ids\":[\"" + dTag + "\"]}]";
  }
}
