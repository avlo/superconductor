package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeDefinitionGenericEventService extends 
   CacheBadgeDefinitionAbstractEventService<BadgeDefinitionGenericEvent> implements CacheBadgeDefinitionGenericEventServiceIF {

  public CacheBadgeDefinitionGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
  }

  @Override
  public Optional<BadgeDefinitionGenericEvent> materialize(@NonNull EventIF incomingBadgeDefinitionGenericEvent) {
    return Optional.of(new BadgeDefinitionGenericEvent(incomingBadgeDefinitionGenericEvent.asGenericEventRecord()));
  }
}
