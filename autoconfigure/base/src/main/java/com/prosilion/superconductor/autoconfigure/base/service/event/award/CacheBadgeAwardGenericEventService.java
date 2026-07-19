package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeAwardGenericEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> implements CacheBadgeAwardGenericEventServiceIF {
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeAwardGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheBadgeDefinitionGenericEventServiceIF);
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  @Override
  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createBadgeAwardEvent(
     @NonNull GenericEventRecord eventRecord,
     @NonNull Function<AddressTag, BadgeDefinitionGenericEvent> badgeDefinitionResolver) {
    return new BadgeAwardGenericEvent<>(eventRecord, badgeDefinitionResolver);
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getBy(@NonNull AddressTag addressTag) {
    return
       cacheKindAddressTagServiceIF
          .getByDirect(
             Kind.BADGE_AWARD_EVENT, addressTag)
          .stream().findFirst()
          .flatMap(this::materialize);
  }
}
