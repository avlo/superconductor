package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeAwardGenericEventService<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends CacheBadgeAwardAbstractEventService<S, T, AddressTag> implements CacheBadgeAwardGenericEventServiceIF<S, T> {

  private final CacheBadgeDefinitionGenericEventServiceIF<S> cacheBadgeDefinitionGenericEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeAwardGenericEventService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF<S> cacheBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheReferenceEventTagServiceIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  @Override
  protected Optional<S> getBadgeDefinition(@NonNull AddressTag addressTag) {
    return cacheBadgeDefinitionGenericEventServiceIF.getBy(addressTag);
  }

  @Override
  protected T createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, S> badgeDefinitionResolver) {
    return (T) new BadgeAwardGenericEvent<S>(eventRecord, badgeDefinitionResolver);
  }

  @Override
  public Optional<T> getBy(@NonNull AddressTag addressTag) {
    return cacheKindAddressTagServiceIF.getBy(Kind.BADGE_AWARD_EVENT, addressTag)
       .stream().findFirst().flatMap(this::materialize);
  }
}
