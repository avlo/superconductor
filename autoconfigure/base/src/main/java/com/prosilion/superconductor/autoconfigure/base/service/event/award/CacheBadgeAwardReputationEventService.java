package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.DurationFactory;
import org.springframework.lang.NonNull;

// TODO: likely replaceable by CacheBadgeAwardGenericEventService
@Slf4j
public class CacheBadgeAwardReputationEventService implements CacheBadgeAwardReputationEventServiceIF {
  public static final String NON_EXISTENT_EVENT_TAG = "BadgeDefinitionReputationEvent with eventId [%s] and url [%s] not found";
  public static final String NON_EXISTENT_ADDRESS_TAG_S = "AddressTag [%s] references non-existent BadgeDefinitionReputationEvent";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardReputationEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException {
    Optional<GenericEventRecord> unpopulatedBadgeAwardReputationEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedBadgeAwardReputationEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeAwardReputationEvent.get()));
  }

  @Override
  public BadgeAwardReputationEvent materialize(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    List<AddressTag> incomingBadgeAwardReputationEventAddressTags =
        Filterable.getTypeSpecificTags(AddressTag.class, incomingBadgeAwardReputationEvent);

    if (incomingBadgeAwardReputationEventAddressTags.size() != 1)
      throw new NostrException(
          String.format("BadgeAwardReputationEvent [%s] requires a single AddressTag but had [%s]",
              EventIF.serialize(incomingBadgeAwardReputationEvent.asGenericEventRecord()),
              incomingBadgeAwardReputationEventAddressTags.size()));

    List<BadgeDefinitionReputationEvent> badgeDefinitionReputationEvents = incomingBadgeAwardReputationEventAddressTags.stream()
        .map(this::apply)
        .map(this::apply)
        .flatMap(Optional::stream)
        .toList();

    Function<AddressTag, BadgeDefinitionReputationEvent> addressTagBadgeDefinitionReputationEventFxn = addressTag ->
        badgeDefinitionReputationEvents.stream()
            .filter(badgeDefinitionReputationEvent ->
                badgeDefinitionReputationEvent.asAddressTag().equals(addressTag)).findFirst().orElseThrow(() ->
                new NostrException(
                    String.format("Found BadgeDefinitionReputationEvents [%s] does not contain AddressTag [%s] ",
                        badgeDefinitionReputationEvents,
                        addressTag)));

    BadgeAwardReputationEvent reconstructedBadgeAwardReputationEvent = new BadgeAwardReputationEvent(
        incomingBadgeAwardReputationEvent.asGenericEventRecord(),
        addressTagBadgeDefinitionReputationEventFxn);

//    log.info("saving BadgeAwardReputationEvent event with eventId [{}] ...", incomingBadgeAwardReputationEvent.getId());
//    cacheServiceIF.save(incomingBadgeAwardReputationEvent);
//    log.info("...done");
    return reconstructedBadgeAwardReputationEvent;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEventTagEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException {
    Optional<GenericEventRecord> unpopulatedBadgeAwardReputationEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedBadgeAwardReputationEvent.isEmpty())
      throw new NostrException(String.format(NON_EXISTENT_EVENT_TAG, eventId, url));

    return getBadgeDefinitionReputationEvent(unpopulatedBadgeAwardReputationEvent.get(), url);
  }

  private String getRelayTagUrl(GenericEventRecord genericEventRecord) {
    return Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord)
        .findFirst()
        .map(relayTag ->
            Optional.of(relayTag).orElseThrow(() ->
                new NostrException("")))
        .map(RelayTag::getRelay)
        .map(Relay::getUrl).orElseThrow(() ->
            new NostrException(""));
  }

  private Optional<BadgeDefinitionReputationEvent> getBadgeDefinitionReputationEvent(@NonNull GenericEventRecord genericEventRecord, @NonNull String url) throws JsonProcessingException {
    return cacheBadgeDefinitionReputationEventService.getEvent(genericEventRecord.getId(), url);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }

  @SneakyThrows
  private GenericEventRecord apply(AddressTag addressTag) {
    return cacheDereferenceAddressTagServiceIF.getEvent(addressTag, DurationFactory.of(10, TimeUnit.SECONDS))
        .orElseThrow(() ->
            new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG_S, addressTag)));
  }

  @SneakyThrows
  private Optional<BadgeDefinitionReputationEvent> apply(GenericEventRecord genericEventRecord) {
    return getBadgeDefinitionReputationEvent(genericEventRecord,
        getRelayTagUrl(genericEventRecord));
  }
}
