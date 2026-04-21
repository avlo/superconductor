package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.subdivisions.client.RequestSubscriber;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.subdivisions.client.reactive.SingleRelaySubscriptionsManager;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAbstractTagServiceIF;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class CacheDereferenceAbstractTagService<T extends ReferencedAbstractEventTag> implements CacheDereferenceAbstractTagServiceIF<T> {
  protected final CacheServiceIF cacheServiceIF;
  private final String superconductorRelayUrl;
  private final Duration requestTimeoutDuration;
  static int padding = 4;
  private final static String blankPadding = " ".repeat(padding);

  public CacheDereferenceAbstractTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl,
      @NonNull Duration requestTimeoutDuration) {
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.requestTimeoutDuration = requestTimeoutDuration;
    log.debug("Ctor() loaded CacheDereferenceAbstractTagService relay URL: {}", superconductorRelayUrl);
  }

  abstract Optional<GenericEventRecord> getLocalEventFxn(T tag);

  abstract Filters getAbstractTagFilters(T abstractTag);

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull T abstractTag) {
    Optional<GenericEventRecord> localGenericEventRecordOptional = getLocalEventFxn(abstractTag);

    if (localGenericEventRecordOptional.isPresent()) {
      log.debug("... returning local AbstractTag, id: [{}], URL: [{}]",
          localGenericEventRecordOptional.get().getId(),
          abstractTag.getRelay().getUrl());
      return localGenericEventRecordOptional;
//      log.debug("... found BUT TEMP NOT RETURNING, TESTING MULTI-HOP REQUEST, local AbstractTag, id: [{}], URL: [{}]",
//          localGenericEventRecordOptional.get().getId(),
//          abstractTag.getRelay().getUrl());
    }

    String recommendedRelayUrl = Optional.ofNullable(abstractTag.getRelay().getUrl()).orElseThrow(() ->
        new NostrException(
            String.format("AbstractTag [%s] does not contain a (valid) url", abstractTag)));

//    if (recommendedRelayUrl.equals(superconductorRelayUrl)) {
//      log.debug("AbstractTag [{}] has local url [{}], yet event not found locally (likely not yet saved), so return Optional.empty()", abstractTag, recommendedRelayUrl);
//      return Optional.empty();
//    }

    log.debug("local AbstractTag not found, calling remoteEventSupplier...");
    return getRemoteEventGenericEventRecord(abstractTag, recommendedRelayUrl);
  }

  private Optional<GenericEventRecord> getRemoteEventGenericEventRecord(T abstractTag, String relayUrl) {
    Optional<GenericEventRecord> optionalGenericEventRecord = sendConsolidatorReq(
        relayUrl,
        getAbstractTagFilters(abstractTag));

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }

  private Optional<GenericEventRecord> sendConsolidatorReq(String relayUrl, Filters apply) {
    ReqMessage reqMessage = new ReqMessage(generateRandomHex64String(), apply);

    String lineBreak = "\n";
    log.debug("... sendConsolidatorReq() (1 of 3) sending request message to\n  URL: {}\n  using subscriberId:  [{}]\n  and filters:\n    {}",
        relayUrl,
        reqMessage.getSubscriptionId(),
        apply.toString().replace(lineBreak, Strings.concat(lineBreak, blankPadding))
    );

//    TODO: finalize which awaitXXX() variant given below awaitXXX() options
    List<BaseMessage> eventList = awaitUsingRequestSubscriberFxns(reqMessage, relayUrl);
//    List<BaseMessage> eventList = awaitUsingCompletableFuture(reqMessage, relayUrl);

    log.debug("... sendConsolidatorReq() (2 of 3) retrieved results...");
    Optional<GenericEventRecord> first = getGenericEvents(eventList).findFirst();
    log.debug("... sendConsolidatorReq() (3 of 3) returning results:\n  {}",
        first.map(GenericEventRecord::createPrettyPrintJson).map(s -> Strings.concat("SUCCESS:\n  ", s))
            .orElse("FAILED: getReqConsolidatorResult EMPTY"));
    return first;
  }

  private List<BaseMessage> awaitOriginal(ReqMessage reqMessage, String relayUrl) {
    log.debug("awaitOriginal (0of1) new NostrSingleRequestService().send(reqMessage, relayUrl, requestTimeoutDuration)...");
    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(reqMessage, relayUrl, requestTimeoutDuration);
    log.debug("awaitOriginal (1of1) ... complete. returned baseMessages (count {}):", baseMessages.size());
    return baseMessages;
  }

  private List<BaseMessage> awaitUsingRequestSubscriberFxns(ReqMessage reqMessage, String relayUrl) {
    log.debug("awaitUsingRequestSubscriberFxns (0of5) inside await()...");
    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>(requestTimeoutDuration);
    NostrSingleRequestService nostrSingleRequestService = new NostrSingleRequestService();
    log.debug("awaitUsingRequestSubscriberFxns (1of5) ... calling nostrSingleRequestService.send(reqMessage, relayUrl, subscriber) using ReqMessage...");
    printDebug(reqMessage);
    SingleRelaySubscriptionsManager manager = nostrSingleRequestService.send(reqMessage, relayUrl, subscriber);
    log.debug("awaitUsingRequestSubscriberFxns (2of5) ... done, calling subscriber.getItems() ...");
    List<BaseMessage> baseMessages = subscriber.getItems();
    log.debug("awaitUsingRequestSubscriberFxns (3of5) ... done, calling subscriber.dispose() ...");
    subscriber.dispose();
    log.debug("awaitUsingRequestSubscriberFxns (4of5) ... done, calling manager.closeAllSessions() ...");
    manager.closeAllSessions();
    log.debug("awaitUsingRequestSubscriberFxns (5of5) ... complete. returned baseMessages (count {}):", baseMessages.size());
    baseMessages.forEach(CacheDereferenceAbstractTagService::printDebug);
    return baseMessages;
  }

  private List<BaseMessage> awaitUsingCompletableFuture(ReqMessage reqMessage, String relayUrl) {
    log.debug("awaitUsingCompletableFuture (0of5) inside awaitRxR()...");
    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>(requestTimeoutDuration);

    CompletableFuture<Void> voidCompletableFutureNostrRequestService = CompletableFuture.runAsync(() ->
            new NostrSingleRequestService().send(reqMessage, relayUrl, subscriber)
        , Executors.newVirtualThreadPerTaskExecutor());
    log.debug("awaitUsingCompletableFuture (1of5) voidCompletableFutureNostrRequestService send() checkpoint");

    RequestSubscriber.await(
        Duration.of(5000, ChronoUnit.MILLIS),
        voidCompletableFutureNostrRequestService::isDone);
    log.debug("awaitUsingCompletableFuture (2of5) voidCompletableFutureNostrRequestService isDone checkpoint");

    CompletableFuture<List<BaseMessage>> getBaseMessagesCompletableFuture = CompletableFuture.supplyAsync(() ->
            subscriber.getItems()
        , Executors.newVirtualThreadPerTaskExecutor());
    log.debug("awaitUsingCompletableFuture (3of5) getBaseMessagesCompletableFuture eventList.set(subscriber.getItems()) checkpoint");

    List<BaseMessage> baseMessages = null;
    getBaseMessagesCompletableFuture.thenApply(baseMessagesList ->
        baseMessages.addAll(baseMessagesList)).join();
    log.debug("awaitUsingCompletableFuture (4of5) getBaseMessagesCompletableFuture isDone checkpoint");

    log.debug("awaitUsingCompletableFuture (5of5) returning baseMessages:");
    baseMessages.forEach(baseMessage -> log.debug("  " + baseMessage.toString()));
    return baseMessages;
  }

  private static void printDebug(BaseMessage baseMessage) {
    String encode;
    try {
      encode = baseMessage.encode();
      log.debug(encode);
    } catch (JsonProcessingException e) {
      log.debug("printDebug(BaseMessage baseMessage) shit the bed, just print baseMessage");
      log.debug(baseMessage.toString());
    }
  }

  private Stream<GenericEventRecord> getGenericEvents(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .map(EventIF::asGenericEventRecord);
  }

  private String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", "");
  }

  private Optional<EventIF> filterEventMessageEvent(BaseMessage returnedBaseMessage) {
    Optional<EventIF> eventIF = Optional.of(returnedBaseMessage)
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent);
    return eventIF;
  }
}
