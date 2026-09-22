package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardCanonicalEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionGenericEventServiceIF;
import java.util.function.Function;
import lombok.NonNull;

public class CacheBadgeAwardCanonicalEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionGenericEvent, BadgeAwardCanonicalEvent> implements CacheBadgeAwardCanonicalEventServiceIF {
  public CacheBadgeAwardCanonicalEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF,
       cacheKindAddressTagServiceIF);
  }

  @Override
  protected boolean supports(@NonNull GenericEventRecord eventRecord) {
    return eventRecord.findFirstTag(ExternalIdentityTag.class).isEmpty();
  }

  @Override
  protected BadgeAwardCanonicalEvent createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, BadgeDefinitionGenericEvent> badgeDefinitionResolver) {
    return new BadgeAwardCanonicalEvent(eventRecord, badgeDefinitionResolver);
  }
}
