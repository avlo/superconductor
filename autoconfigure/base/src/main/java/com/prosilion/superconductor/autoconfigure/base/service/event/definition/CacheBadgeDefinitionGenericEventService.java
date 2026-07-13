package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeDefinitionGenericEventService<T extends BadgeDefinitionGenericEvent> extends CacheBadgeDefinitionAbstractEventService<T> implements CacheBadgeDefinitionGenericEventServiceIF<T> {
  private final CacheServiceIF cacheServiceIF;

  public CacheBadgeDefinitionGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    super(cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<T> materialize(@NonNull EventIF incomingBadgeDefinitionGenericEvent) {
    return Optional.of((T) new BadgeDefinitionGenericEvent(incomingBadgeDefinitionGenericEvent.asGenericEventRecord()));
  }

  @Override
  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super
       .getEvent(eventId, relay)
       .or(() ->
          cacheServiceIF.getEventsByKindAndEventTag(
                getKind(),
                new EventTag(eventId)).stream().findFirst()
             .flatMap(this::materialize));
  }
}
