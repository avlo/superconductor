package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeAwardGenericEventAuxService extends CacheBadgeAwardAbstractEventAuxService<BadgeDefinitionGenericEventAux, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> implements CacheBadgeAwardGenericEventAuxServiceIF<BadgeDefinitionGenericEventAux, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> {
  private static final String BADGE_DEFN_NOT_FOUND = "getBadgeDefinitionEvent(incomingBadgeAwardGenericEvent) returned EMPTY optional";
  private final CacheBadgeDefinitionGenericEventAuxServiceIF cacheBadgeDefinitionGenericEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeAwardGenericEventAuxService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventAuxServiceIF cacheBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheReferenceEventTagServiceIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  @Override
  public Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> materialize(@NonNull EventIF incomingBadgeAwardGenericEvent) {
    log.debug("... materialize incomingBadgeAwardGenericEvent:\n{}", incomingBadgeAwardGenericEvent.createPrettyPrintJson());
    Optional<BadgeDefinitionGenericEventAux> by = cacheBadgeDefinitionGenericEventServiceIF
       .getBy(incomingBadgeAwardGenericEvent.requireFirstTag(AddressTag.class));

    Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> badgeAwardGenericEvent = by
       .map(event -> new BadgeAwardGenericEventAux<>(
          incomingBadgeAwardGenericEvent.asGenericEventRecord(),
          addressTag -> event));

    return badgeAwardGenericEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> getBy(@NonNull AddressTag addressTag) {
    return materialize(cacheKindAddressTagServiceIF.getBy(Kind.BADGE_AWARD_EVENT, addressTag).getFirst());
  }
}
