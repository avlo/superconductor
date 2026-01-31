package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
  public static final String NON_EXISTENT_FORMULA_EVENT = "BadgeDefinitionReputationEvent [%s] contains AddressTag [%s] referencing non-existent FormulaEvent [%s]";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheFormulaEventService = cacheFormulaEventService;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionReputationEvent = cacheDereferenceEventTagServiceIF.getEvent(new EventTag(eventId));
    if (unpopulatedBadgeDefinitionReputationEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeDefinitionReputationEvent.get()));
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(@NonNull GenericEventRecord incomingBadgeDefinitionReputationEvent) {
    List<FormulaEvent> formulaEvents = getFormulaEvents(incomingBadgeDefinitionReputationEvent);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        incomingBadgeDefinitionReputationEvent, addressTag ->
        formulaEvents.stream().filter(formulaEvent ->
            formulaEvent.asAddressTag().equals(addressTag)).findFirst().orElseThrow());

    return badgeDefinitionReputationEvent;
  }

  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
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
