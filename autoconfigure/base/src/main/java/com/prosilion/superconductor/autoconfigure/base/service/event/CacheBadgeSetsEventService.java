package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeAwardEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService implements CacheBadgeSetsEventServiceIF, EventMaterializer<BadgeSetsEvent> {
  private final CacheServiceIF cacheServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardEventServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheCuratedBadgeAwardEventServiceIF = cacheCuratedBadgeAwardEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize(EventIF incomingBadgeSetsEvent):\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    Optional<BadgeSetsEvent> badgeSetsEvent = cacheBadgeDefinitionReputationEventServiceIF
       .getByExpanded(
          incomingBadgeSetsEvent.requireFirstTag(AddressTag.class))
       .map(badgeDefinitionReputationEvent ->
          new BadgeSetsEvent(
             incomingBadgeSetsEvent.asGenericEventRecord(),
             badgeDefinitionReputationEvent,
             getCuratedBadgeAwardGenericEventList(incomingBadgeSetsEvent)));
    badgeSetsEvent.map(cacheServiceIF::save);
    return badgeSetsEvent;
  }

  private @NonNull List<CuratedBadgeAwardGenericEvent> getCuratedBadgeAwardGenericEventList(@NonNull EventIF incomingBadgeSetsEvent) {
    List<EventTag> eventTagList = incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class);
    
    List<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEvents = eventTagList.stream()
       .map(eventTag -> cacheCuratedBadgeAwardEventServiceIF
          .getEvent(eventTag.eventId(), eventTag.requireRelay()))
       .flatMap(Optional::stream)
       .distinct()
       .toList();

    return curatedBadgeAwardGenericEvents;
  }

  @Override
  public Optional<BadgeSetsEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheServiceIF.getEventByEventId(eventId).flatMap(this::materialize);
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
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
