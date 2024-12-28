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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
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

  public void save(@NonNull String eventJson) throws IOException {
    OkMessage createEvent =
        createNostrEvent(
            eventJson
        );

    assertTrue(createEvent.getFlag());
  }

  public void save(@NonNull GenericEvent event) throws IOException {
    OkMessage okMessageClassifiedListing =
        createNostrEvent(
            new EventMessageFactory(event).create()
        );

    assertTrue(okMessageClassifiedListing.getFlag());
  }

  private OkMessage createNostrEvent(@NonNull String eventJson) throws IOException {
    return getOkMessage(sendEvent(eventJson));
  }

  private OkMessage createNostrEvent(@NonNull EventMessage eventMessage) throws IOException {
    return getOkMessage(sendEvent(eventMessage));
  }

  @NotNull
  private static OkMessage getOkMessage(List<String> received) {
    Optional<String> last = Streams.findLast(received.stream());
    return last
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
    log.info("received relay response:");
    log.info("\n" + events.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    return events;
  }

//  --------------------------------------------------------------------
//  REQUESTS
//  --------------------------------------------------------------------

  public String get(@NonNull String eventId) throws IOException, ExecutionException, InterruptedException {
    String event =
        sendNostrRequest(
            eventId,
            GenericEvent.class
        );

    return event;
  }


  private String sendNostrRequest(
      @NonNull String eventId,
      @NonNull Class<GenericEvent> type) throws IOException, ExecutionException, InterruptedException {
    List<String> returnedEvents = sendRequest(
        eventId
    );

    System.out.println("55555555555555555");
    System.out.println("after REQUEST:");
    System.out.printf("key:\n  [%s]\n", eventId);
    System.out.println("-----------------");
    System.out.println("returnedEvents:");
    System.out.printf(returnedEvents.stream().map(event -> String.format("  %s\n", event)).collect(Collectors.joining()));
    System.out.println("55555555555555555");
    Stream<BaseMessage> baseMessageStream = returnedEvents.stream().map(baseMessage -> new BaseMessageDecoder<>().decode(baseMessage));

    Stream<BaseMessage> baseMessageStream1 = baseMessageStream.filter(baseMessage -> !baseMessage.getCommand().equalsIgnoreCase("EOSE"));

    Stream<BaseMessage> baseMessageStream2 = baseMessageStream1.filter(EventMessage.class::isInstance);

    Stream<EventMessage> eventMessageStream = baseMessageStream2.map(EventMessage.class::cast);

    Stream<GenericEvent> genericEventStream = eventMessageStream.map(eventMessage -> (GenericEvent) eventMessage.getEvent());

    Stream<String> stringStream = genericEventStream.map(event -> new BaseEventEncoder<>(event).encode());

//    Stream<T> tStream = stringStream.map(encode -> new GenericEventDecoder<>(type).decode(encode));

//    Optional<T> max = tStream.max((a, b) -> {
//      System.out.println("a: " + a);
//      System.out.println("b: " + b);
//      return getCompare(a, b);
//    });
//    T latestDatedEvent = max.orElseThrow();

    List<String> list = stringStream.toList();
    String last = list.getLast();
    String latestDatedEvent = last;

//    System.out.println("2222222222222222222");
//    System.out.println("2222222222222222222");
//    System.out.println(latestDatedEvent);
//    System.out.println("2222222222222222222");
//    System.out.println("2222222222222222222");
    return latestDatedEvent;
  }

  private List<String> sendRequest(@NonNull String keyWhichIsTheEventId) throws ExecutionException, InterruptedException, IOException {
    String subscriberPrefixEventIdSuffix = subscriberIdPrefix + keyWhichIsTheEventId;
    final StandardWebSocketClient webSocketClientIF = requestSocketClientMap.get(subscriberPrefixEventIdSuffix);
    if (webSocketClientIF != null) {
      System.out.printf("3333333333333 existing REQ socket\nkey:\n  [%s]\nsocket:\n  [%s]\n\n", subscriberPrefixEventIdSuffix, webSocketClientIF.getClientSession().getId());
      List<String> events = webSocketClientIF.getEvents();
      System.out.println("-------------");
      System.out.println("socket getEvents():");
      events.forEach(event -> System.out.printf("  %s\n", event));
      System.out.println("33333333333\n");
      return events;
    }

    requestSocketClientMap.put(subscriberPrefixEventIdSuffix, new StandardWebSocketClient(relayUri
//        , sslBundles
    ));

    final StandardWebSocketClient newWebSocketClientIF = requestSocketClientMap.get(subscriberPrefixEventIdSuffix);
    System.out.printf("222222222222 new REQ socket\nkey:\n  [%s]\nsocket:\n  [%s]\n\n", subscriberPrefixEventIdSuffix, newWebSocketClientIF.getClientSession().getId());
    newWebSocketClientIF.send(createReqJson(subscriberPrefixEventIdSuffix, keyWhichIsTheEventId));
    List<String> events = newWebSocketClientIF.getEvents();
    System.out.println("-------------");
    System.out.println("socket getEvents():");
    events.forEach(event -> System.out.printf("  %s\n", event));
    System.out.println("222222222222\n");
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
