package com.prosilion.superconductor.autoconfigure.base.service.event.award;

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
import java.util.function.Function;
import lombok.NonNull;

public class CacheBadgeAwardReputationEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> implements CacheBadgeAwardReputationEventServiceIF {
  public CacheBadgeAwardReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheKindAddressTagServiceIF);
  }

  @Override
  protected boolean supports(@NonNull GenericEventRecord eventRecord) {
    return eventRecord.findFirstTag(ExternalIdentityTag.class).isPresent();
  }

  @Override
  protected BadgeAwardReputationEvent createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, BadgeDefinitionReputationEvent> badgeDefinitionResolver) {
    return new BadgeAwardReputationEvent(eventRecord, badgeDefinitionResolver);
  }
}
