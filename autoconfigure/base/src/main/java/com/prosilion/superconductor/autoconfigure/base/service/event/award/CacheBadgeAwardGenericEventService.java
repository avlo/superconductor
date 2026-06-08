package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;

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
  public BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> materialize(@NonNull EventIF incomingBadgeAwardGenericEvent) {
    log.debug("... materialize incomingBadgeAwardGenericEvent:\n{}", incomingBadgeAwardGenericEvent.createPrettyPrintJson());

    Optional<BadgeDefinitionGenericEvent> dbBadgeDefinitionGenericEvent = cacheBadgeDefinitionGenericEventServiceIF
        .getBy(incomingBadgeAwardGenericEvent.asGenericEventRecord().requireFirstTag(AddressTag.class));

    if (dbBadgeDefinitionGenericEvent.isEmpty())
      throw new NostrException(BADGE_DEFN_NOT_FOUND);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
        new BadgeAwardGenericEvent<>(
            incomingBadgeAwardGenericEvent.asGenericEventRecord(),
            addressTag -> dbBadgeDefinitionGenericEvent.get());
    log.debug("... return materialized badgeAwardGenericEvent:\n{}", badgeAwardGenericEvent.createPrettyPrintJson());

    return badgeAwardGenericEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    List<GenericEventRecord> genericEventRecords = cacheKindAddressTagServiceIF.getBy(Kind.BADGE_AWARD_EVENT, pubKeyTag, addressTag);
    return genericEventRecords.stream().map(this::materialize).findFirst();
  }
}
