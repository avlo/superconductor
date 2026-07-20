package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeSetsEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceDecorIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeAwardEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService extends CacheCuratedEventService<BadgeSetsEvent> implements CacheBadgeSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceDecorIF cacheBadgeDefinitionReputationEventServiceDecorIF;
  private final CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceDecorIF cacheBadgeDefinitionReputationEventServiceDecorIF,
     @NonNull CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF) {
    super(cacheServiceIF);
    this.cacheServiceIF = cacheServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceDecorIF = cacheBadgeDefinitionReputationEventServiceDecorIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
  }

  @Override
  public Optional<BadgeSetsEvent> materialize(@NonNull EventIF incomingBadgeSetsEvent) {
    log.debug("materialize(EventIF incomingBadgeSetsEvent):\n  {}", incomingBadgeSetsEvent.createPrettyPrintJson());
    return
       cacheBadgeDefinitionReputationEventServiceDecorIF
          .getByExpanded(
             incomingBadgeSetsEvent.requireFirstTag(AddressTag.class))
          .map(badgeDefinitionReputationEvent ->
             new BadgeSetsEvent(
                incomingBadgeSetsEvent.asGenericEventRecord(),
                badgeDefinitionReputationEvent,
                incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class).stream()
                   .map(eventTag -> cacheCuratedBadgeAwardGenericEventServiceIF
                      .getEvent(eventTag.eventId(), eventTag.requireRelay()))
                   .flatMap(Optional::stream)
                   .distinct()
                   .toList()));
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
