package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;

@Slf4j
public class CacheBadgeDefinitionGenericEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionGenericEvent> implements CacheBadgeDefinitionGenericEventServiceIF {

  public CacheBadgeDefinitionGenericEventService(
      @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
      @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    super(cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
  }

  @Override
  public BadgeDefinitionGenericEvent materialize(@NonNull EventIF incomingBadgeDefinitionGenericEvent) {
    log.debug("... materialize(incomingBadgeDefinitionGenericEvent)...\n{}", incomingBadgeDefinitionGenericEvent.createPrettyPrintJson());

    return new BadgeDefinitionGenericEvent(incomingBadgeDefinitionGenericEvent.asGenericEventRecord());
  }
}
