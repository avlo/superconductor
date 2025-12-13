package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardReputationEventService implements CacheBadgeAwardReputationEventServiceIF {
  public static final String NON_EXISTENT_EVENT_ID_S = "BadgeAwardReputationEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public void save(BadgeAwardReputationEvent incomingBadgeAwardReputationEvent) {
    List<AddressTag> incomingBadgeAwardReputationEventAddressTags = incomingBadgeAwardReputationEvent.getContainedEventsAsAddressTags();

    if (incomingBadgeAwardReputationEventAddressTags.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", incomingBadgeAwardReputationEvent.serialize(), incomingBadgeAwardReputationEventAddressTags.size()));

    incomingBadgeAwardReputationEventAddressTags
        .forEach(addressTag ->
            cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        String.join("", NON_EXISTENT_EVENT_ID_S, "[%s]"),
                        incomingBadgeAwardReputationEvent.serialize(),
                        incomingBadgeAwardReputationEvent.getId(),
                        addressTag))));

    log.info("saving BadgeAwardReputationEvent event with eventId [{}] ...", incomingBadgeAwardReputationEvent.getId());
    cacheServiceIF.save(incomingBadgeAwardReputationEvent);
    log.info("...done");
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getEvent(String eventId) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardReputationEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedBadgeAwardReputationEvent.isEmpty())
      return Optional.empty();

    List<AddressTag> addressTagsOfBadgeDefinitionEvent = Filterable.getTypeSpecificTags(AddressTag.class, unpopulatedBadgeAwardReputationEvent.get()).stream().toList();

    if (addressTagsOfBadgeDefinitionEvent.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedBadgeAwardReputationEvent, addressTagsOfBadgeDefinitionEvent.size()));

    GenericEventRecord firstAddressTagAsReputationDefinitionEvent = cacheDereferenceAddressTagServiceIF.getEvent(addressTagsOfBadgeDefinitionEvent.getFirst()).orElseThrow();
    BadgeDefinitionReputationEvent cacheBadgeDefinitionReputationEvent = cacheBadgeDefinitionReputationEventService.getEvent(firstAddressTagAsReputationDefinitionEvent.getId()).orElseThrow();

    return Optional.of(cacheDereferenceAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedBadgeAwardReputationEvent.orElseThrow(),
        BadgeAwardReputationEvent.class,
        addressTag ->
            cacheBadgeDefinitionReputationEvent));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
