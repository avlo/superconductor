package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericVoteEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericVoteEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardGenericVoteEventService implements CacheBadgeAwardGenericVoteEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG_S = "BadgeAwardAbstractEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardGenericVoteEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public void save(@NonNull BadgeAwardGenericVoteEvent incomingBadgeAwardReputationEvent) {
    List<AddressTag> incomingBadgeAwardReputationEventAddressTags = incomingBadgeAwardReputationEvent.getContainedAddressableEvents();

    if (incomingBadgeAwardReputationEventAddressTags.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", incomingBadgeAwardReputationEvent.serialize(), incomingBadgeAwardReputationEventAddressTags.size()));

    incomingBadgeAwardReputationEventAddressTags
        .forEach(addressTag ->
            cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        String.join("", NON_EXISTENT_ADDRESS_TAG_S, "[%s]"),
                        incomingBadgeAwardReputationEvent.serialize(),
                        incomingBadgeAwardReputationEvent.getId(),
                        addressTag))));

    log.info("saving BadgeAwardReputationEvent event with eventId [{}] ...", incomingBadgeAwardReputationEvent.getId());
    cacheServiceIF.save(incomingBadgeAwardReputationEvent);
    log.info("...done");
  }

  @Override
  public Optional<BadgeAwardGenericVoteEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardGenericVoteEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedBadgeAwardGenericVoteEvent.isEmpty())
      return Optional.empty();

    List<AddressTag> addressTagsOfBadgeAwardGenericVoteEvent = Filterable.getTypeSpecificTags(AddressTag.class, unpopulatedBadgeAwardGenericVoteEvent.get()).stream().toList();

    if (addressTagsOfBadgeAwardGenericVoteEvent.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedBadgeAwardGenericVoteEvent, addressTagsOfBadgeAwardGenericVoteEvent.size()));

    GenericEventRecord firstAddressTagAsReputationDefinitionEvent = cacheDereferenceAddressTagServiceIF.getEvent(addressTagsOfBadgeAwardGenericVoteEvent.getFirst()).orElseThrow();
    BadgeDefinitionReputationEvent cacheBadgeDefinitionReputationEvent = cacheBadgeDefinitionReputationEventService.getEvent(firstAddressTagAsReputationDefinitionEvent.getId()).orElseThrow();

    return Optional.of(cacheDereferenceAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedBadgeAwardGenericVoteEvent.orElseThrow(),
        BadgeAwardGenericVoteEvent.class,
        addressTag ->
            cacheBadgeDefinitionReputationEvent));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
