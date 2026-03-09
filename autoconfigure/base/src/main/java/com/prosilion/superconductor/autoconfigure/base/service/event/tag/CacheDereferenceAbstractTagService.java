package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.reactive.ReactiveRequestConsolidator;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAbstractTagServiceIF;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.awaitility.Awaitility;
import org.reactivestreams.Subscription;
import org.springframework.lang.NonNull;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;

@Slf4j
public abstract class CacheDereferenceAbstractTagService<T extends ReferencedAbstractEventTag> extends BaseSubscriber<BaseMessage> implements CacheDereferenceAbstractTagServiceIF<T> {
  private final List<BaseMessage> items = Collections.synchronizedList(new ArrayList<>());
  private final AtomicBoolean completed = new AtomicBoolean(false);
  private Subscription subscription;

  private final ReactiveRequestConsolidator reactiveRequestConsolidator;
  protected final CacheServiceIF cacheServiceIF;

  private final String superconductorRelayUrl;
  private final Duration requestTimeoutDuration;


  public CacheDereferenceAbstractTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl,
      @NonNull Duration requestTimeoutDuration) {
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.requestTimeoutDuration = requestTimeoutDuration;
    this.reactiveRequestConsolidator = new ReactiveRequestConsolidator();
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
    }

    String recommendedRelayUrl = Optional.ofNullable(abstractTag.getRelay().getUrl()).orElseThrow(() ->
        new NostrException(
            String.format("AbstractTag [%s] does not contain a (valid) url", abstractTag)));

    if (recommendedRelayUrl.equals(superconductorRelayUrl)) {
      log.debug("AbstractTag [%s] has local url [%s], yet event not found locally (likely not yet saved), so return Optional.empty()", abstractTag, recommendedRelayUrl);
      return Optional.empty();
//      throw new NostrException(
//          String.format("AbstractTag [%s] has local url [%s], yet event not found locally", abstractTag, recommendedRelayUrl));
    }

    log.debug("local AbstractTag not found, calling remoteEventSupplier...");
    return getRemoteEventGenericEventRecord(abstractTag, recommendedRelayUrl);
  }

  private Optional<GenericEventRecord> getRemoteEventGenericEventRecord(T abstractTag, String relayUrl) {
    sendConsolidatorReq(
        relayUrl,
        getAbstractTagFilters(abstractTag));

    Optional<GenericEventRecord> optionalGenericEventRecord = getReqConsolidatorResult();

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }

  @SneakyThrows
  private void sendConsolidatorReq(String relayUrl, Filters apply) {
    ReqMessage reqMessage = new ReqMessage(
        generateRandomHex64String(),
        apply);
    log.debug("sending reactiveRequestConsolidator request message:\n  {}", Util.prettyFormatJson(reqMessage.encode()));
    reactiveRequestConsolidator.send(reqMessage, this, relayUrl);
  }

  private Stream<GenericEventRecord> getGenericEvents(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .map(EventIF::asGenericEventRecord);
  }

  public Optional<GenericEventRecord> getReqConsolidatorResult() {
    log.debug("... getReqConsolidatorResult() (1 of 3) called, waiting [{}] for results...", requestTimeoutDuration);
    Awaitility.await()
        .timeout(requestTimeoutDuration)
        .untilTrue(completed);

    List<BaseMessage> eventList = List.copyOf(items);
    log.debug("... getReqConsolidatorResult() (2 of 3) retrieved results...");
    items.clear();
    completed.set(false);
    Optional<GenericEventRecord> first = getGenericEvents(eventList).findFirst();
    log.debug("... getReqConsolidatorResult() (3 of 3) returning results:\n  {}",
        first.map(GenericEventRecord::createPrettyPrintJson).map(s -> Strings.concat("SUCCESS:\n  ", s))
            .orElse("FAILED: getReqConsolidatorResult EMPTY"));
    return first;
  }

  @Override
  public void hookOnSubscribe(@NonNull Subscription subscription) {
    this.subscription = subscription;
//    this.subscription.request(Long.MAX_VALUE);
    this.subscription.request(1L);
  }

  @Override
  public void hookOnNext(@NonNull BaseMessage value) {
    log.debug("=====================================");
    log.debug("=====================================");
    completed.set(false);
//    subscription.request(Long.MAX_VALUE);
    subscription.request(1L);
    completed.set(true);
    items.add(value);
    log.debug("=====================================");
    log.debug("=====================================");
  }

  @Override
  protected void hookOnComplete() {
    log.debug("+++++++++++++++++++++++++++++++++++++");
    log.debug("+++++++++++++++++++++++++++++++++++++");
    completed.set(true);
    log.debug("completed.set(true)");
    log.debug("+++++++++++++++++++++++++++++++++++++");
    log.debug("+++++++++++++++++++++++++++++++++++++");
  }

  @Override
  public boolean isDisposed() {
    boolean disposed = super.isDisposed();
    log.debug("0000000000000000000000000000000000000");
    log.debug("0000000000000000000000000000000000000");
    log.debug("isDisposed()? {}", disposed);
    log.debug("0000000000000000000000000000000000000");
    log.debug("0000000000000000000000000000000000000");
    return disposed;
  }

  @Override
  protected void hookOnError(Throwable throwable) {
    log.debug("1111111111111111111111111111111111111");
    log.debug("1111111111111111111111111111111111111");
    log.debug("hookOnError()? {}", throwable.getMessage());
    log.debug("1111111111111111111111111111111111111");
    log.debug("1111111111111111111111111111111111111");
    super.hookOnError(throwable);
  }

  @Override
  protected void hookOnCancel() {
    log.debug("2222222222222222222222222222222222222");
    log.debug("2222222222222222222222222222222222222");
    log.debug("hookOnCancel()");
    log.debug("2222222222222222222222222222222222222");
    log.debug("2222222222222222222222222222222222222");
    super.hookOnCancel();
  }

  @Override
  protected void hookFinally(SignalType type) {
    log.debug("3333333333333333333333333333333333333");
    log.debug("3333333333333333333333333333333333333");
    log.debug("hookFinally() SignalType:  [{}]", type.name());
    log.debug("3333333333333333333333333333333333333");
    log.debug("3333333333333333333333333333333333333");
    super.hookFinally(type);
  }

  @Override
  public @NonNull Context currentContext() {
    log.debug("-------------------------------------");
    log.debug("-------------------------------------");
    Context context = super.currentContext();
    log.debug("currentContext() context:  {}", context);
    log.debug("-------------------------------------");
    log.debug("-------------------------------------");
    return context;
  }

  private String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", "");
  }
}
