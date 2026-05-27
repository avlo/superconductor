package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardGenericEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> implements CacheBadgeAwardGenericEventServiceIF<BadgeDefinitionGenericEvent, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
  public static final String MISSING_ADDRESS_TAG = "incoming BadgeAwardGenericEvent %s did not contain an AddressTag";
  private static final String BADGE_DEFN_NOT_FOUND = "getBadgeDefinitionEvent(incomingBadgeAwardGenericEvent) returned EMPTY optional";
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;

  public CacheBadgeAwardGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    super(cacheDereferenceEventTagServiceIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> materialize(@NonNull EventIF incomingBadgeAwardGenericEvent) {
    log.debug("... materialize incomingBadgeAwardGenericEvent:\n{}", incomingBadgeAwardGenericEvent.createPrettyPrintJson());

    Optional<BadgeDefinitionGenericEvent> dbBadgeDefinitionGenericEvent = getBadgeDefinitionEvent(incomingBadgeAwardGenericEvent.asGenericEventRecord());

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
  protected Optional<BadgeDefinitionGenericEvent> getBadgeDefinitionEvent(@NonNull GenericEventRecord genericEventRecord) {
    return cacheBadgeDefinitionGenericEventServiceIF.getAddressTagEvent(genericEventRecord);
  }
}
