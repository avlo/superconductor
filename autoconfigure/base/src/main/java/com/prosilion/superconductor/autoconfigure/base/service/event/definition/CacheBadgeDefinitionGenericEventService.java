package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionGenericEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionGenericEvent> implements CacheBadgeDefinitionGenericEventServiceIF {

  public CacheBadgeDefinitionGenericEventService(
      @NonNull CacheReferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheReferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    super(cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
  }

  @Override
  public Optional<BadgeDefinitionGenericEvent> getAddressTagEvent(@NonNull GenericEventRecord genericEventRecord) {
    return getExistingDefinitionEvent(genericEventRecord);
  }

  @Override
  public BadgeDefinitionGenericEvent materialize(@NonNull EventIF incomingBadgeDefinitionGenericEvent) {
    log.debug("... materialize(incomingBadgeDefinitionGenericEvent)...\n{}", incomingBadgeDefinitionGenericEvent.createPrettyPrintJson());

    return new BadgeDefinitionGenericEvent(incomingBadgeDefinitionGenericEvent.asGenericEventRecord());
  }
}
