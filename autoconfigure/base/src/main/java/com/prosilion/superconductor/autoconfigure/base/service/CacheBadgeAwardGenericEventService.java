package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeAwardGenericEventService implements CacheBadgeAwardGenericEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG_S = "BadgeAwardAbstractEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheBadgeAwardGenericEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public BadgeAwardGenericEvent reconstruct(@NonNull BadgeAwardGenericEvent incomingBadgeAwardGenericEvent) {
    List<AddressTag> incomingBadgeAwardGenericEventAddressTags = incomingBadgeAwardGenericEvent.getContainedAddressableEvents();

    if (incomingBadgeAwardGenericEventAddressTags.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardGenericEvent [%s] requires a single AddressTag but had [%s]", incomingBadgeAwardGenericEvent.serialize(), incomingBadgeAwardGenericEventAddressTags.size()));

    incomingBadgeAwardGenericEventAddressTags
        .forEach(addressTag ->
            cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        String.join("", NON_EXISTENT_ADDRESS_TAG_S, "[%s]"),
                        incomingBadgeAwardGenericEvent.serialize(),
                        incomingBadgeAwardGenericEvent.getId(),
                        addressTag))));

    return incomingBadgeAwardGenericEvent;
  }

  @Override
  public Optional<BadgeAwardGenericEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardGenericVoteEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedBadgeAwardGenericVoteEvent.isEmpty())
      return Optional.empty();

    List<AddressTag> addressTagsOfBadgeAwardGenericVoteEvent = Filterable.getTypeSpecificTags(AddressTag.class, unpopulatedBadgeAwardGenericVoteEvent.get()).stream().toList();

    if (addressTagsOfBadgeAwardGenericVoteEvent.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", unpopulatedBadgeAwardGenericVoteEvent, addressTagsOfBadgeAwardGenericVoteEvent.size()));

    GenericEventRecord firstAddressTagAsAwardDefinitionEventGenericEventRecord = cacheDereferenceAddressTagServiceIF.getEvent(addressTagsOfBadgeAwardGenericVoteEvent.getFirst()).orElseThrow();
    BadgeDefinitionAwardEvent cacheBadgeDefinitionAwardEvent = getBadgeDefinitionAwardEvent(firstAddressTagAsAwardDefinitionEventGenericEventRecord);

    return Optional.of(cacheDereferenceAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedBadgeAwardGenericVoteEvent.orElseThrow(),
        BadgeAwardGenericEvent.class,
        addressTag ->
            cacheBadgeDefinitionAwardEvent));
  }

  @Override
  public BadgeDefinitionAwardEvent getBadgeDefinitionAwardEvent(@NonNull GenericEventRecord genericEventRecord) {
    GenericEventRecord cacheBadgeDefinitionAwardEventGenericEventRecord = cacheServiceIF.getEventByEventId(genericEventRecord.getId()).orElseThrow();
    BadgeDefinitionAwardEvent cacheBadgeDefinitionAwardEvent = new BadgeDefinitionAwardEvent(cacheBadgeDefinitionAwardEventGenericEventRecord);
    return cacheBadgeDefinitionAwardEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
