package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeAwardGenericEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> implements CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
  private static final String BADGE_DEFN_NOT_FOUND = "getBadgeDefinitionEvent(incomingBadgeAwardGenericEvent) returned EMPTY optional";
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeAwardGenericEventService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheReferenceEventTagServiceIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> materialize(@NonNull EventIF incomingBadgeAwardGenericEvent) {
    log.debug("... materialize incomingBadgeAwardGenericEvent:\n{}", incomingBadgeAwardGenericEvent.createPrettyPrintJson());
    return
       cacheBadgeDefinitionGenericEventServiceIF
          .getBy(
             incomingBadgeAwardGenericEvent.requireFirstTag(AddressTag.class))
          .map(event -> new BadgeAwardGenericEvent<>(
             incomingBadgeAwardGenericEvent.asGenericEventRecord(),
             addressTag -> event));
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getBy(@NonNull AddressTag addressTag) {
    return materialize(cacheKindAddressTagServiceIF.getBy(Kind.BADGE_AWARD_EVENT, addressTag).getFirst());
  }
}
