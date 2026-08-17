package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import lombok.NonNull;

public class CacheBadgeDefinitionGenericEventService extends
   CacheBadgeDefinitionAbstractEventService<BadgeDefinitionGenericEvent> implements CacheBadgeDefinitionGenericEventServiceIF {

  public CacheBadgeDefinitionGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
  }

  @Override
  protected BadgeDefinitionGenericEvent createBadgeDefinitionEvent(@NonNull GenericEventRecord eventRecord) {
    return new BadgeDefinitionGenericEvent(eventRecord);
  }
}
