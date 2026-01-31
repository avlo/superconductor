package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

// TODO: likely replaceable by CacheBadgeAwardGenericEventService
@Slf4j
public class CacheBadgeAwardReputationEventService implements CacheBadgeAwardReputationEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG_S = "BadgeAwardReputationEvent [%s] contains AddressTag [%s] referencing non-existent BadgeDefinitionReputationEvent";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedBadgeAwardReputationEvent = cacheDereferenceEventTagServiceIF.getEvent(new EventTag(eventId));
    if (unpopulatedBadgeAwardReputationEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeAwardReputationEvent.get()));
  }

  @Override
  public BadgeAwardReputationEvent materialize(@NonNull GenericEventRecord incomingBadgeAwardReputationEvent) {
    PublicKey awardRecipientPublicKey = Filterable.getTypeSpecificTags(PubKeyTag.class, incomingBadgeAwardReputationEvent)
        .stream()
        .map(PubKeyTag::getPublicKey)
        .findFirst().orElseThrow();

//    IdentifierTag reputationEventIdentifierTag = Filterable.getTypeSpecificTags(IdentifierTag.class, incomingBadgeAwardReputationEvent)
//        .stream()
//        .findFirst().orElseThrow();

    List<AddressTag> addressTagsOfBadgeDefinitionEvent = Filterable.getTypeSpecificTags(AddressTag.class, incomingBadgeAwardReputationEvent).stream().toList();

    if (addressTagsOfBadgeDefinitionEvent.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]", incomingBadgeAwardReputationEvent, addressTagsOfBadgeDefinitionEvent.size()));

    GenericEventRecord firstAddressTagAsReputationDefinitionEvent = cacheDereferenceAddressTagServiceIF.getEvent(addressTagsOfBadgeDefinitionEvent.getFirst()).orElseThrow();
    BadgeDefinitionReputationEvent cacheBadgeDefinitionReputationEvent = getBadgeDefinitionReputationEvent(firstAddressTagAsReputationDefinitionEvent);

    BadgeAwardReputationEvent existingBadgeAwardReputationEvent =
        getEvent(
            awardRecipientPublicKey,
            incomingBadgeAwardReputationEvent.getPublicKey()
//            , reputationEventIdentifierTag
        )
            .orElse(
                new BadgeAwardReputationEvent(
                    incomingBadgeAwardReputationEvent.asGenericEventRecord(),
                    addressTag -> cacheBadgeDefinitionReputationEvent));

    return reconstruct(existingBadgeAwardReputationEvent);
  }

  private BadgeAwardReputationEvent reconstruct(@NonNull BadgeAwardReputationEvent incomingBadgeAwardReputationEvent) {
    List<AddressTag> incomingBadgeAwardReputationEventAddressTags = incomingBadgeAwardReputationEvent.getContainedAddressableEvents();

    if (incomingBadgeAwardReputationEventAddressTags.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]",
              EventIF.serialize(incomingBadgeAwardReputationEvent.asGenericEventRecord()),
              incomingBadgeAwardReputationEventAddressTags.size()));

    incomingBadgeAwardReputationEventAddressTags
        .forEach(addressTag ->
            cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        String.join("", NON_EXISTENT_ADDRESS_TAG_S, "[%s]"),
                        EventIF.serialize(incomingBadgeAwardReputationEvent.asGenericEventRecord()),
                        incomingBadgeAwardReputationEvent.getId(),
                        addressTag))));

//    log.info("saving BadgeAwardReputationEvent event with eventId [{}] ...", incomingBadgeAwardReputationEvent.getId());
//    cacheServiceIF.save(incomingBadgeAwardReputationEvent);
//    log.info("...done");
    return incomingBadgeAwardReputationEvent;
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getEvent(
      @NonNull PublicKey awardReputationRecipientPublicKey,
      @NonNull PublicKey eventCreatorPublicKey
//      , @NonNull IdentifierTag badgeDefintionIdentifierTag
  ) {
    Optional<GenericEventRecord> matchingDbBadgeAwardReputationEventGenericEventRecord =
        getEventsByKindAndAuthorPublicKey(eventCreatorPublicKey).filter(eventCreatorBadgeAwardGenericEventRecord ->
                eventCreatorBadgeAwardGenericEventRecord.getTags().stream()
                    .filter(PubKeyTag.class::isInstance)
                    .map(PubKeyTag.class::cast)
                    .map(PubKeyTag::getPublicKey)
                    .equals(getEventsByKindAndRecipientPublicKey(awardReputationRecipientPublicKey)
                        .map(GenericEventRecord::getTags).flatMap(Collection::stream)
                        .filter(PubKeyTag.class::isInstance)
                        .map(PubKeyTag.class::cast)
                        .map(PubKeyTag::getPublicKey)
                        .filter(awardReputationRecipientPublicKey::equals).findFirst().stream()))
//            .filter(dbBadgeAwardReputationEvent ->
//                dbBadgeAwardReputationEvent.getTags().contains(badgeDefintionIdentifierTag))
            .findFirst();

    return matchingDbBadgeAwardReputationEventGenericEventRecord.flatMap(genericEventRecord ->
        this.getEvent(genericEventRecord.getId()));
  }

  private Stream<GenericEventRecord> getEventsByKindAndAuthorPublicKey(PublicKey eventCreatorPublicKey) {
    List<GenericEventRecord> eventsByKindAndAuthorPublicKey = cacheServiceIF.getEventsByKindAndAuthorPublicKey(Kind.BADGE_AWARD_EVENT, eventCreatorPublicKey);
    return eventsByKindAndAuthorPublicKey.stream();
  }

  private Stream<GenericEventRecord> getEventsByKindAndRecipientPublicKey(PublicKey awardReceipientPublicKey) {
    List<GenericEventRecord> eventsByKindAndPubKeyTag = cacheServiceIF.getEventsByKindAndPubKeyTag(Kind.BADGE_AWARD_EVENT, awardReceipientPublicKey);
    return eventsByKindAndPubKeyTag.stream();
  }

  private BadgeDefinitionReputationEvent getBadgeDefinitionReputationEvent(@NonNull GenericEventRecord genericEventRecord) {
    return cacheBadgeDefinitionReputationEventService.getEvent(genericEventRecord.getId()).orElseThrow();
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
