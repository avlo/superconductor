package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CacheBadgeAwardAbstractEventService<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> {
  protected final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;

  public CacheBadgeAwardAbstractEventService(@NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
  }

  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize);
  }

  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }

  protected abstract Optional<T> materialize(@NonNull EventIF eventIF);
}
