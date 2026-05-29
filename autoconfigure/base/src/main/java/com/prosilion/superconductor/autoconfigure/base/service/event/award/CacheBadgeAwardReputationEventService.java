package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

// TODO: likely replaceable by CacheBadgeAwardGenericEventService
@Slf4j
public class CacheBadgeAwardReputationEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> implements CacheBadgeAwardReputationEventServiceIF {
  private static final String BADGE_DEFN_NOT_FOUND = "getBadgeDefinitionEvent(addressTagsAsGenericEventRecord) returned EMPTY optional";
  private final CacheReferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardReputationEventService(
      @NonNull CacheReferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheReferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    super(cacheDereferenceEventTagServiceIF);
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public BadgeAwardReputationEvent materialize(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    log.debug("... materialize incomingBadgeAwardReputationEvent:\n{}", incomingBadgeAwardReputationEvent.createPrettyPrintJson());

    List<GenericEventRecord> addressTagsAsGenericEventRecord = cacheDereferenceAddressTagServiceIF.getEventAddressTagsAsGenericEventRecords(incomingBadgeAwardReputationEvent);

    if (addressTagsAsGenericEventRecord.size() != 1)
      throw new NostrException(
          String.format("incomingBadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]",
              incomingBadgeAwardReputationEvent.asGenericEventRecord().createPrettyPrintJson(),
              addressTagsAsGenericEventRecord.size()));

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent =
        cacheBadgeDefinitionReputationEventService.getEvent(
            addressTagsAsGenericEventRecord.getFirst().getId(),
            addressTagsAsGenericEventRecord.getFirst().getRelayTagUrl()).orElseThrow(() ->
            new NostrException(BADGE_DEFN_NOT_FOUND));

    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent(
        incomingBadgeAwardReputationEvent.asGenericEventRecord(),
        addressTag -> badgeDefinitionReputationEvent);
    log.debug("... return materialized badgeAwardReputationEvent:\n{}", badgeAwardReputationEvent.createPrettyPrintJson());

    return badgeAwardReputationEvent;
  }
}
