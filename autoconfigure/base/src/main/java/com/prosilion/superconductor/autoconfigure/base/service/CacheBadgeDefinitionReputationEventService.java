package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG_S = "BadgeDefinitionReputationEvent [%s] contains AddressTag [%s] referencing non-existent FormulaEvent";
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
  public void save(@NonNull BadgeDefinitionReputationEvent incomingBadgeDefinitionReputationEvent) {
    List<AddressTag> incomingBadgeDefinitionReputationEventAddressTags = incomingBadgeDefinitionReputationEvent.getContainedAddressableEvents();

    if (incomingBadgeDefinitionReputationEventAddressTags.isEmpty())
      throw new NostrException(
          String.format("BadgeDefinitionReputationEvent [%s] requires at last one AddressTag", incomingBadgeDefinitionReputationEvent));

    incomingBadgeDefinitionReputationEventAddressTags
        .forEach(addressTag ->
            cacheDereferenceAddressTagServiceIF.getEvent(addressTag).orElseThrow(() ->
                new NostrException(
                    String.format(
                        String.join("", NON_EXISTENT_ADDRESS_TAG_S, "[%s]"),
                        incomingBadgeDefinitionReputationEvent.serialize(),
                        incomingBadgeDefinitionReputationEvent.getId(),
                        addressTag))));

    Optional<GenericEventRecord> optionalBadgeDefinitionReputationEvent =
        cacheDereferenceAddressTagServiceIF.getEvent(
            incomingBadgeDefinitionReputationEvent.asAddressTag());

    if (optionalBadgeDefinitionReputationEvent.isEmpty()) {
      log.debug("saving new FormulaEvent {}...", incomingBadgeDefinitionReputationEvent);
      cacheServiceIF.save(incomingBadgeDefinitionReputationEvent);
      log.debug("...done");
      return;
    }

    log.debug("Identical BadgeDefinitionReputationEvent already exists, save not required, just return");
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId) {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionReputationEvent = cacheServiceIF.getEventByEventId(eventId);
    if (unpopulatedBadgeDefinitionReputationEvent.isEmpty())
      return Optional.empty();

    List<AddressTag> addressTagsOfFormulaEvents = Filterable.getTypeSpecificTags(AddressTag.class, unpopulatedBadgeDefinitionReputationEvent.get()).stream().toList();

    List<GenericEventRecord> formulaEventsAsGenericEventRecords =
        addressTagsOfFormulaEvents.stream()
            .map(cacheDereferenceAddressTagServiceIF::getEvent)
            .flatMap(Optional::stream)
            .toList();

    List<FormulaEvent> formulaEvents = formulaEventsAsGenericEventRecords.stream()
        .map(GenericEventRecord::getId)
        .map(cacheFormulaEventService::getEvent)
        .flatMap(Optional::stream).toList();

    Function<AddressTag, FormulaEvent> fxn = addressTag ->
        formulaEvents.stream().filter(formulaEvent ->
            formulaEvent.getIdentifierTag().equals(addressTag.getIdentifierTag())).findFirst().orElseThrow();

    return Optional.of(cacheDereferenceAddressTagServiceIF.createTypedFxnEvent(
        unpopulatedBadgeDefinitionReputationEvent.orElseThrow(),
        BadgeDefinitionReputationEvent.class,
        fxn));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
