package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardAbstractEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionAbstractEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;

public abstract class CacheBadgeAwardAbstractEventService<
   S extends AddressableEvent,
   T extends BadgeAwardAbstractEvent<S>> implements CacheBadgeAwardAbstractEventServiceIF<S, T> {

  private final CacheServiceIF cacheServiceIF;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheBadgeDefinitionAbstractEventServiceIF<S> cacheBadgeDefinitionAbstractEventService;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeAwardAbstractEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionAbstractEventServiceIF<S> cacheBadgeDefinitionAbstractEventService,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheBadgeDefinitionAbstractEventService = cacheBadgeDefinitionAbstractEventService;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheServiceIF.getEventByEventId(eventId).flatMap(this::materialize)
//       .or(() ->
//          cacheServiceIF.getEventsByKindAndEventTag(
//                Kind.CURATION_SETS_BADGE_DEFINITION_EVENT,
//                new EventTag(eventId)).stream().findFirst()
//             .flatMap(this::materialize))
       .or(() -> cacheReferenceEventTagServiceIF.getEvent(eventId, relay)
          .flatMap(this::materialize));
  }

  public Optional<T> materialize(@NonNull EventIF eventIF) {
    return cacheBadgeDefinitionAbstractEventService
       .getByExpanded(
          eventIF.requireFirstTag(AddressTag.class))
       .map(sType ->
          createBadgeAwardEvent(eventIF.asGenericEventRecord(), addressTag -> sType));
  }

  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }

  @Override
  public final Optional<T> getByDirect(@NonNull AddressTag addressTag) {
    return cacheKindAddressTagServiceIF
       .getByDirect(getKind(), addressTag).stream()
       .filter(this::supports)
       .findFirst()
       .flatMap(this::materialize);
  }

  protected abstract boolean supports(@NonNull GenericEventRecord eventRecord);

  protected abstract T createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, S> badgeDefinitionResolver);
}
