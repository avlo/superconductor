package com.prosilion.superconductor.autoconfigure.curation.service.event.sets;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService implements CacheBadgeSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize incomingBadgeSetsEvent:\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent =
       cacheBadgeDefinitionReputationEventServiceIF.getByExpanded(incomingBadgeSetsEvent.requireFirstTag(AddressTag.class))
          .orElseThrow();

    List<EventTag> eventTagList = incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class);
    List<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEvents = eventTagList.stream()
       .map(eventTag -> {
         log.debug("calling cacheCuratedBadgeAwardGenericEventServiceIF.getByExpanded(eventTag):\n  [{}]", eventTag);
         Optional<CuratedBadgeAwardGenericEvent> event = cacheCuratedBadgeAwardGenericEventServiceIF
            .getByExpanded(eventTag);
         log.debug("returned Optional CuratedBadgeAwardGenericEvent:\n  {}", event.map(CuratedBadgeAwardGenericEvent::createPrettyPrintJson).orElse("[ EMPTY OPTIONAL]"));
         return event;
       })
       .flatMap(Optional::stream)
       .distinct()
       .toList();
    log.debug("returned List CuratedBadgeAwardGenericEvent:\n  {}",
       curatedBadgeAwardGenericEvents.stream().map(CuratedBadgeAwardGenericEvent::createPrettyPrintJson).collect(Collectors.joining(",\n")));

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       incomingBadgeSetsEvent.asGenericEventRecord(),
       badgeDefinitionReputationEvent,
       curatedBadgeAwardGenericEvents);

    log.debug("return materialized BadgeSetsEvent:\n  {}", badgeSetsEvent.createPrettyPrintJson());
    return Optional.of(badgeSetsEvent);
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize);
  }

  @Override
  public Optional<BadgeSetsEvent> getByDirect(@NonNull AddressTag referencedAbstractEventTag) {
    return materializeFirst(cacheKindAddressTagServiceIF.getByDirect(getKind(), referencedAbstractEventTag));
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag referencedAbstractEventTag) {
    return materializeFirst(cacheKindAddressTagServiceIF.getByDirect(getKind(), pubKeyTag, referencedAbstractEventTag));
  }

  @Override
  public List<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag) {
    return materializeList(cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag)).toList();
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag));
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    log.debug("inside getBy(PubKeyTag, IdentifierTag):\n  [{}]\n  [{}]", pubKeyTag.getPublicKey().toHexString(), identifierTag.getUuid());
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag));
  }

  @Override
  public Optional<BadgeSetsEvent> getByExpanded(@NonNull AddressTag referencedAbstractEventTag) {
    log.debug("inside getByExpanded(AddressTag):\n  {}", referencedAbstractEventTag.toStringPrettyPrint());
    Optional<GenericEventRecord> byExpanded = cacheReferenceAddressTagServiceIF.getByExpanded(referencedAbstractEventTag);
    log.debug("returned getByExpanded(AddressTag):\n  {}", byExpanded.map(GenericEventRecord::createPrettyPrintJson).orElse(
       "[ EMPTY OPTIONAL ]"));
    Optional<BadgeSetsEvent> badgeSetsEvent = byExpanded.flatMap(this::materialize);
    log.debug("returning materialized Optional BadgeSetsEvent :\n  {}", badgeSetsEvent.map(BadgeSetsEvent::createPrettyPrintJson).orElse(
       "[ EMPTY OPTIONAL ]"));
    return badgeSetsEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
