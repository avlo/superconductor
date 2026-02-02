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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
  public static final String NON_EXISTENT_ADDRESSTAG_EVENT = "%s getFormulaEvents(genericEventRecord) calling cacheDereferenceAddressTagServiceIF.getEvent(addressTag) contains AddressTag [%s] referencing non-existent FormulaEvent";
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
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull String url) {
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
            formulaEvent.asAddressTag().equals(addressTag)).findFirst().orElseThrow(
//                () ->
//            new NostrException(
//                String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord))
        )
    );

    return badgeDefinitionReputationEvent;
  }

  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
    List<AddressTag> addressTagsOfFormulaEvents = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord).toList();

    if (addressTagsOfFormulaEvents.isEmpty())
      throw new NostrException(
          String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));

    Map<GenericEventRecord, String> formulaEventsAsGenericEventRecords =
        addressTagsOfFormulaEvents.stream()
            .collect(
                Collectors.toMap(addressTag ->
                        cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                            new NostrException(
                                String.format(NON_EXISTENT_ADDRESSTAG_EVENT, getClass().getSimpleName(), addressTag))),
                    addressTag -> addressTag.getRelay().getUrl()));

    if (!Objects.equals(addressTagsOfFormulaEvents.size(), formulaEventsAsGenericEventRecords.size()))
      throw new NostrException(
          String.format(NON_EXISTENT_FORMULA_EVENT, genericEventRecord, addressTagsOfFormulaEvents, formulaEventsAsGenericEventRecords));

    List<FormulaEvent> formulaEvents = formulaEventsAsGenericEventRecords
        .entrySet().stream()
        .map(genericEventRecordStringEntry ->
            cacheFormulaEventService.getEvent(genericEventRecordStringEntry.getKey().getId(), genericEventRecordStringEntry.getValue()))
        .flatMap(Optional::stream).toList();

    return formulaEvents;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
