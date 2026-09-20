package com.prosilion.superconductor.autoconfigure.curation.service.event.sets;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.AbstractCacheCuratedEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeSetsEventService extends AbstractCacheCuratedEventService<BadgeSetsEvent, BadgeSetsEvent> implements CacheBadgeSetsEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;

  public CacheBadgeSetsEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF) {
    super(instanceIdentity, relayUrl, cacheServiceIF);
    this.cacheServiceIF = cacheServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
  }

  @Override
  protected BadgeSetsEvent createFrom(@NonNull GenericEventRecord genericEventRecord) {
    log.debug("inside createFrom(genericEventRecord):\n  {}", genericEventRecord.createPrettyPrintJson());

    AddressTag referencedAbstractEventTag = genericEventRecord.requireFirstTag(AddressTag.class);
    log.debug("calling cacheBadgeDefinitionReputationEventServiceIF.getByExpanded(genericEventRecord.requireFirstTag(AddressTag):\n  {}", referencedAbstractEventTag);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent =
       cacheBadgeDefinitionReputationEventServiceIF.getByExpanded(referencedAbstractEventTag)
          .orElseThrow();
    log.debug("returned BadgeDefinitionReputationEvent:\n  {}", badgeDefinitionReputationEvent.createPrettyPrintJson());

    List<EventTag> eventTagList = genericEventRecord.getTypeSpecificTags(EventTag.class);
    List<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEvents = eventTagList.stream()
       .map(eventTag -> {
         log.debug("calling cacheCuratedBadgeAwardGenericEventServiceIF.getByExpanded(eventTag): {}, url: {}", eventTag.getEventId(), eventTag.getRecommendedRelayUrl());
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
       genericEventRecord.asGenericEventRecord(),
       badgeDefinitionReputationEvent,
       curatedBadgeAwardGenericEvents);

    log.debug("return materialized BadgeSetsEvent:\n  {}", badgeSetsEvent.createPrettyPrintJson());
    return badgeSetsEvent;
  }

  @Override
  public Optional<BadgeSetsEvent> getByDirect(@NonNull AddressTag addressTag) {
    return findOrCurate(
       () -> findFirstByAddressTag(addressTag),
       () -> materializeFirst(cacheKindAddressTagServiceIF.getByDirect(getKind(), addressTag)),
       formulaEvent -> formulaEvent.getRelay().orElseThrow());
  }

  @Override
  public Optional<BadgeSetsEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return getByDirect(addressTag).stream().filter(badgeSetsEvent ->
          badgeSetsEvent.getAwardRecipientPublicKey().equals(pubKeyTag.getPublicKey()))
       .findFirst();
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
  protected BadgeSetsEvent createFromFetched(@NonNull BadgeSetsEvent baseEvent, @NonNull Relay relay) {
    return baseEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
