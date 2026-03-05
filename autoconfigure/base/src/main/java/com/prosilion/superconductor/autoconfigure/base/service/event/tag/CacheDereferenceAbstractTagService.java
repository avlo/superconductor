package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.autoconfigure.base.config.NostrRelayReqConsolidatorService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAbstractTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class CacheDereferenceAbstractTagService<T extends ReferencedAbstractEventTag> implements CacheDereferenceAbstractTagServiceIF<T> {
  protected final CacheServiceIF cacheServiceIF;
  @Getter
  private final String superconductorRelayUrl;
  private final NostrRelayReqConsolidatorService nostrRelayReqConsolidatorService;

  public CacheDereferenceAbstractTagService(CacheServiceIF cacheServiceIF, String superconductorRelayUrl, NostrRelayReqConsolidatorService nostrRelayReqConsolidatorService) {
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.nostrRelayReqConsolidatorService = nostrRelayReqConsolidatorService;
    log.debug("Ctor() loaded CacheDereferenceEventTag relay URL: {}", superconductorRelayUrl);
  }

  abstract Optional<GenericEventRecord> getEventFxn(T tag);

  abstract Filters getAbstractTagFilters(T abstractTag);

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull T abstractTag) {
    Optional<GenericEventRecord> eventFxn = getEventFxn(abstractTag);

    boolean present = eventFxn.isPresent();
    if (present) {
      log.debug("... returning local AbstractTag, id: [{}], URL: [{}]\n",
          eventFxn.get().getId(),
          abstractTag.getRelay().getUrl());
      return eventFxn;
    }

    log.debug("local AbstractTag not found, calling remoteEventSupplier...");
    return getRemoteEventGenericEventRecord(abstractTag);
  }

  private Optional<GenericEventRecord> getRemoteEventGenericEventRecord(T abstractTag) {
    Relay relay = abstractTag.getRelay();

    String recommendedRelayUrl = Optional.ofNullable(relay.getUrl()).orElseThrow(() ->
        new NostrException(
            String.format("AbstractTag [%s] does not contain a (valid) url", abstractTag)));

    Filters abstractTagFilters = getAbstractTagFilters(abstractTag);

    Optional<GenericEventRecord> optionalGenericEventRecord =
        remoteEventSupplier(
            recommendedRelayUrl,
            abstractTagFilters);

    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event {} saved to local DB", genericEventRecord));
    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);

    return optionalGenericEventRecord;
  }

  private Optional<GenericEventRecord> remoteEventSupplier(String relayUrl, Filters apply) {
    if (relayUrl.equals(getSuperconductorRelayUrl()))
      return Optional.empty();

    ReqMessage reqMessage = new ReqMessage(
        generateRandomHex64String(),
        apply);

    List<BaseMessage> send = getNostrRelayReqConsolidatorService(reqMessage, relayUrl);

    List<GenericEventRecord> genericEvents = getGenericEvents(send).toList();
    Optional<GenericEventRecord> first = genericEvents.stream().findFirst();
    return first;
  }

  public List<BaseMessage> getNostrRelayReqConsolidatorService(ReqMessage reqMessage, String relayUrl) {
    try {
      log.debug("nostrRelayReqConsolidatorService.send(reqMessage, relayUrl) content:\n  {},\nrelayUrl:  [{}]",
          reqMessage.encode(), relayUrl);
    } catch (JsonProcessingException e) {
      log.debug("encode exception nostrRelayReqConsolidatorService.send(reqMessage, relayUrl) content:\n  {},\nrelayUrl:  [{}]",
          reqMessage, relayUrl);
    }
    List<BaseMessage> send = nostrRelayReqConsolidatorService.send(reqMessage, relayUrl);
    return send;
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
}
