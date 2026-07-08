package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.CurationSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheCurationSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheCurationSetsEventService implements CacheCurationSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheReferenceEventTagService cacheReferenceEventTagService;

  public CacheCurationSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagService cacheReferenceEventTagService,
     @NonNull CacheReferenceAddressTagService cacheReferenceAddressTagService,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheReferenceEventTagService = cacheReferenceEventTagService;
  }

  @Override
  public Optional<CurationSetsEvent> materialize(@NonNull EventIF incomingCurationSetsEvent) {
    log.debug("materialize(EventIF incomingCurationSetsEvent):\n  {}", incomingCurationSetsEvent.createPrettyPrintJson());
    return Optional.of(new CurationSetsEvent(incomingCurationSetsEvent.asGenericEventRecord()));
  }

  @Override
  public Optional<CurationSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    log.debug("inside getEvent(eventId, relay):\n  event [{}]\n  relay [{}]", eventId, relay);
    log.debug("calling cacheDereferenceEventTagService.getEvent(eventId, relay)...");
    Optional<GenericEventRecord> event = cacheReferenceEventTagService.getEvent(eventId, relay);
    log.debug("... cacheDereferenceEventTagService.getEvent(eventId, relay) returned:\n  [{}]",
       event.map(GenericEventRecord::createPrettyPrintJson));
    return event.flatMap(this::materialize);
  }

  @Override
  public Optional<CurationSetsEvent> getBy(@NonNull EventTag eventTag) {
    log.debug("inside getBy(@NonNull AddressTag [{}]", eventTag);
    log.debug("calling cacheReferenceAddressTagService.getBy(eventTag)...");
    return cacheServiceIF.getEventsByKindAndEventTag(getKind(), eventTag).stream()
       .map(this::materialize).flatMap(Optional::stream).findFirst();
  }

  @Override
  public List<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

  @Override
  public Optional<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag).stream()
       .map(this::materialize).flatMap(Optional::stream).findFirst();
  }

  @Override
  public List<CurationSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag).stream()
       .map(this::materialize).flatMap(Optional::stream).toList();
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS;
  }
}
