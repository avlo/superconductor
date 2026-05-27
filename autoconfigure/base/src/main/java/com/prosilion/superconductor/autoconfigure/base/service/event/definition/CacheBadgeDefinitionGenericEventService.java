package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionGenericEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionGenericEvent> implements CacheBadgeDefinitionGenericEventServiceIF {

  public CacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
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
