package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
  public static final String NON_EXISTENT_FORMULA_EVENT = "BadgeDefinitionReputationEvent [%s] contains AddressTag [%s] referencing non-existent FormulaEvent [%s]";
  private final CacheServiceIF cacheServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheFormulaEventService = cacheFormulaEventService;
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    GenericEventRecord incomingBadgeDefinitionReputationEventAsGenericEventRecord = (GenericEventRecord) incomingBadgeDefinitionReputationEvent;

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        incomingBadgeDefinitionReputationEventAsGenericEventRecord, addressTag ->
        getFormulaEvents(incomingBadgeDefinitionReputationEventAsGenericEventRecord).stream().filter(formulaEvent ->
            formulaEvent.asAddressTag().equals(addressTag)).findFirst().orElseThrow());

    return reconstruct(badgeDefinitionReputationEvent);
  }

  public BadgeDefinitionReputationEvent reconstruct(@NonNull BadgeDefinitionReputationEvent incomingBadgeDefinitionReputationEvent) {
    List<AddressTag> incomingBadgeDefinitionReputationEventAddressTags = incomingBadgeDefinitionReputationEvent.getContainedAddressableEvents();

    if (incomingBadgeDefinitionReputationEventAddressTags.isEmpty())
      throw new NostrException(
          String.format("BadgeDefinitionReputationEvent [%s] requires at last one AddressTag", incomingBadgeDefinitionReputationEvent));

    incomingBadgeDefinitionReputationEventAddressTags
        .forEach(addressTag ->
            cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        NON_EXISTENT_FORMULA_EVENT,
                        incomingBadgeDefinitionReputationEvent.serialize(),
                        incomingBadgeDefinitionReputationEvent.getId(),
                        addressTag))));

    Optional<GenericEventRecord> optionalBadgeDefinitionReputationEvent =
        cacheDereferenceAddressTagServiceIF.getEvent(
            incomingBadgeDefinitionReputationEvent.asAddressTag());

    if (optionalBadgeDefinitionReputationEvent.isEmpty()) {
      return incomingBadgeDefinitionReputationEvent;
    }

    log.debug("Identical BadgeDefinitionReputationEvent already exists, save not required, just return");
    return incomingBadgeDefinitionReputationEvent;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId) {
    return cacheServiceIF.getEventByEventId(eventId)
        .map(genericEventRecord ->
            new BadgeDefinitionReputationEvent(genericEventRecord, addressTag ->
                getFormulaEvents(genericEventRecord).stream()
                    .filter(formulaEvent ->
                        formulaEvent.getIdentifierTag().equals(addressTag.getIdentifierTag()))
                    .findFirst().orElseThrow()));

  }

  @Override
  public List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
    List<AddressTag> addressTagsOfFormulaEvents = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord).toList();

    if (addressTagsOfFormulaEvents.isEmpty())
      throw new NostrException(
          String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));

    List<GenericEventRecord> formulaEventsAsGenericEventRecords =
        addressTagsOfFormulaEvents.stream()
            .map(cacheDereferenceAddressTagServiceIF::getEvent)
            .flatMap(Optional::stream)
            .toList();

    if (!Objects.equals(addressTagsOfFormulaEvents.size(), formulaEventsAsGenericEventRecords.size()))
      throw new NostrException(
          String.format(NON_EXISTENT_FORMULA_EVENT, genericEventRecord, addressTagsOfFormulaEvents, formulaEventsAsGenericEventRecords));

    List<FormulaEvent> formulaEvents = formulaEventsAsGenericEventRecords.stream()
        .map(GenericEventRecord::getId)
        .map(cacheFormulaEventService::getEvent)
        .flatMap(Optional::stream).toList();
    return formulaEvents;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
