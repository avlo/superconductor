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
import com.prosilion.subdivisions.client.reactive.NostrSingleRelayRequestService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAbstractTagServiceIF;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class CacheDereferenceAbstractTagService<T extends ReferencedAbstractEventTag> implements CacheDereferenceAbstractTagServiceIF<T> {
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
    Optional<GenericEventRecord> optionalGenericEventRecord = sendConsolidatorReq(
        relayUrl,
        getAbstractTagFilters(abstractTag));

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }

  @SneakyThrows
  private Optional<GenericEventRecord> sendConsolidatorReq(String relayUrl, Filters apply) {
    ReqMessage reqMessage = new ReqMessage(
        generateRandomHex64String(),
        apply);
    log.debug("reactiveRequestConsolidator request to URL:\n  [{}]\nwith ReqMessage:\n  {}", relayUrl, Util.prettyFormatJson(reqMessage.encode()));
    List<BaseMessage> eventList = new NostrSingleRelayRequestService(relayUrl).send(reqMessage, requestTimeoutDuration);
//    nostrRequestService.disconnect();

    log.debug("... getReqConsolidatorResult() (2 of 3) retrieved results...");
    Optional<GenericEventRecord> first = getGenericEvents(eventList).findFirst();
    log.debug("... getReqConsolidatorResult() (3 of 3) returning results:\n  {}",
        first.map(GenericEventRecord::createPrettyPrintJson).map(s -> Strings.concat("SUCCESS:\n  ", s))
            .orElse("FAILED: getReqConsolidatorResult EMPTY"));
    return first;
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
