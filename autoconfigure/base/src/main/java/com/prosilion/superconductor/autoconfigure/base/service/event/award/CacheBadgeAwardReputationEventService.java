package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeAwardReputationEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> implements CacheBadgeAwardReputationEventServiceIF {
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeAwardReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheBadgeDefinitionReputationEventServiceIF);
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  @Override
  protected BadgeAwardReputationEvent createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, BadgeDefinitionReputationEvent> badgeDefinitionResolver) {
    return new BadgeAwardReputationEvent(eventRecord, badgeDefinitionResolver);
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getByDirect(@NonNull AddressTag addressTag) {
    return
       cacheKindAddressTagServiceIF
          .getByDirect(
             Kind.BADGE_AWARD_EVENT,
             addressTag).stream()
          .filter(genericEventRecord ->
             genericEventRecord.findFirstTag(ExternalIdentityTag.class)
                .isPresent()).findFirst()
          .flatMap(this::materialize);
  }
}
