package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.DurationFactory;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG_EVENT = "%s getFormulaEvents(genericEventRecord) calling cacheDereferenceAddressTagServiceIF.getEvent(addressTag) contains AddressTag [%s] referencing non-existent FormulaEvent";
  public static final String NON_EXISTENT_FORMULA_EVENT = "BadgeDefinitionReputationEvent [%s] contains AddressTag [%s] referencing non-existent FormulaEvent [%s]";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheFormulaEventService cacheFormulaEventService;
  private final CacheServiceIF cacheServiceIF;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
    this.cacheFormulaEventService = cacheFormulaEventService;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionReputationEvent =
        cacheDereferenceEventTagServiceIF.getEvent(eventId, url, DurationFactory.of(10, TimeUnit.SECONDS));
    if (unpopulatedBadgeDefinitionReputationEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeDefinitionReputationEvent.get()));
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    List<FormulaEvent> formulaEvents = getFormulaEvents(incomingBadgeDefinitionReputationEvent.asGenericEventRecord());
    return new BadgeDefinitionReputationEvent(
        incomingBadgeDefinitionReputationEvent.asGenericEventRecord(), addressTag ->
        formulaEvents.stream().filter(formulaEvent ->
            formulaEvent.asAddressTag().equals(addressTag)).findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, incomingBadgeDefinitionReputationEvent))));
  }

  @SneakyThrows
  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
    List<AddressTag> addressTagsOfFormulaEvents = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord).toList();

    if (addressTagsOfFormulaEvents.isEmpty())
      throw new NostrException(
          String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));

    Map<GenericEventRecord, String> formulaEventsAsGenericEventRecords =
        new HashMap<>();
    for (AddressTag addressTag : addressTagsOfFormulaEvents) {
      if (formulaEventsAsGenericEventRecords.put(cacheDereferenceAddressTagServiceIF.getEvent(addressTag, DurationFactory.of(10, TimeUnit.SECONDS)).orElseThrow(() ->
          new NostrException(
              String.format(NON_EXISTENT_ADDRESS_TAG_EVENT, getClass().getSimpleName(), addressTag))), addressTag.getRelay().getUrl()) != null) {
        throw new IllegalStateException("Duplicate key");
      }
    }

    if (!Objects.equals(addressTagsOfFormulaEvents.size(), formulaEventsAsGenericEventRecords.size()))
      throw new NostrException(
          String.format(NON_EXISTENT_FORMULA_EVENT, genericEventRecord, addressTagsOfFormulaEvents, formulaEventsAsGenericEventRecords));

    List<FormulaEvent> formulaEvents;
    formulaEvents = formulaEventsAsGenericEventRecords
        .entrySet().stream()
        .map(this::apply)
        .flatMap(Optional::stream).toList();

    return formulaEvents;
  }

  @Override
  public List<BadgeDefinitionReputationEvent> getExistingReputationDefinitionEvents() {
    return cacheServiceIF.getByKind(
            Kind.BADGE_DEFINITION_EVENT).stream()
        .filter(ger ->
            !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, ger).isEmpty())
        .map(this::apply)
        .flatMap(Optional::stream).toList();
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }

  @SneakyThrows
  private Optional<FormulaEvent> apply(Map.Entry<GenericEventRecord, String> genericEventRecordStringEntry) {
    return cacheFormulaEventService.getEvent(genericEventRecordStringEntry.getKey().getId(), genericEventRecordStringEntry.getValue());
  }

  @SneakyThrows
  private Optional<BadgeDefinitionReputationEvent> apply(GenericEventRecord genericEventRecord) {
    return getEvent(genericEventRecord.getId(),
        Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord)
            .findFirst()
            .map(RelayTag::getRelay)
            .map(Relay::getUrl).orElseThrow());
  }
}
