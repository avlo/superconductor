package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardAbstractEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CacheBadgeAwardAbstractEventService<
   S extends AddressableEvent,
   T extends BadgeAwardAbstractEvent<S>> implements CacheBadgeAwardAbstractEventServiceIF<S, T, AddressTag> {
  
  protected final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;

  public CacheBadgeAwardAbstractEventService(@NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
  }

  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize);
  }

  public Optional<T> materialize(@NonNull EventIF eventIF) {
    return getBadgeDefinition(eventIF.requireFirstTag(AddressTag.class))
       .map(sType ->
          createBadgeAwardEvent(
             eventIF.asGenericEventRecord(),
             addressTag -> sType));
  }

  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }

  protected abstract Optional<S> getBadgeDefinition(@NonNull AddressTag addressTag);

  protected abstract T createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, S> badgeDefinitionResolver);
}
