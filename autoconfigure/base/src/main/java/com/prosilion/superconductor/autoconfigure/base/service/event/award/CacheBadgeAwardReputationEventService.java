package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

// TODO: likely replaceable by CacheBadgeAwardGenericEventService
@Slf4j
public class CacheBadgeAwardReputationEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> implements CacheBadgeAwardReputationEventServiceIF {
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardReputationEventService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    super(cacheReferenceEventTagServiceIF);
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public Optional<BadgeAwardReputationEvent> materialize(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    return cacheBadgeDefinitionReputationEventService
       .getBy(
          incomingBadgeAwardReputationEvent.requireFirstTag(AddressTag.class))
       .map(event ->
          new BadgeAwardReputationEvent(
             incomingBadgeAwardReputationEvent.asGenericEventRecord(),
             addressTag -> event));
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getBy(@NonNull AddressTag addressTag) {
    return
       cacheKindAddressTagServiceIF
          .getBy(
             Kind.BADGE_AWARD_EVENT,
             addressTag).stream()
          .filter(genericEventRecord ->
             genericEventRecord.findFirstTag(ExternalIdentityTag.class).isPresent()).findFirst()
          .flatMap(genericEventRecord ->
             getEvent(
                genericEventRecord.getId(),
                genericEventRecord.requireFirstTag(RelayTag.class).getRelay()));
  }
}
