package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeDefinitionGenericEventAuxService extends CacheBadgeDefinitionAbstractEventAuxService<BadgeDefinitionGenericEventAux> implements CacheBadgeDefinitionGenericEventAuxServiceIF {

  public CacheBadgeDefinitionGenericEventAuxService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    super(cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
  }

  @Override
  public Optional<BadgeDefinitionGenericEventAux> materialize(@NonNull EventIF incomingBadgeDefinitionGenericEvent) {
    log.debug("... materialize(incomingBadgeDefinitionGenericEvent)...\n{}", incomingBadgeDefinitionGenericEvent.createPrettyPrintJson());

    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(incomingBadgeDefinitionGenericEvent.asGenericEventRecord());

    return Optional.of(new BadgeDefinitionGenericEventAux(
       badgeDefinitionGenericEvent,
       incomingBadgeDefinitionGenericEvent.getRelayTag().orElse(null)));
  }
}
