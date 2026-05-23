package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService implements CacheBadgeDefinitionReputationEventServiceIF {
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
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull String url) {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionReputationEvent =
        cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    if (unpopulatedBadgeDefinitionReputationEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeDefinitionReputationEvent.get()));
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    log.debug("... materialize(@NonNull EventIF incomingBadgeDefinitionReputationEvent)...\n{}", incomingBadgeDefinitionReputationEvent.createPrettyPrintJson());

    List<FormulaEvent> formulaEvents = getFormulaEvents(incomingBadgeDefinitionReputationEvent.asGenericEventRecord());
    log.debug("... formulaEvent count: [{}]", formulaEvents.size());
    log.debug("... materialized formulaEvents ...\n  {}", Util.prettyPrintGenericEventRecords(formulaEvents.stream().map(FormulaEvent::getGenericEventRecord).toList()));

    log.debug("formulaEvents as addressTags:");
    formulaEvents.stream().map(AddressableEvent::asAddressTag).map(Util::prettyPrintAddressTags).toList().forEach(log::debug);

    return new BadgeDefinitionReputationEvent(
        incomingBadgeDefinitionReputationEvent.asGenericEventRecord(), addressTag ->
        formulaEvents.stream().filter(formulaEvent ->
            isEquals(addressTag, formulaEvent)).findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, incomingBadgeDefinitionReputationEvent))));
  }

  private boolean isEquals(AddressTag addressTag, FormulaEvent formulaEvent) {
    return formulaEvent.asAddressTag().equals(addressTag);
  }

  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
    log.debug("getFormulaEvents(@NonNull GenericEventRecord genericEventRecord):\n{}", genericEventRecord.createPrettyPrintJson());

    List<AddressTag> addressTagsOfFormulaEventsOriginalList = Filterable.getTypeSpecificTagsStream(AddressTag.class, genericEventRecord).toList();
    log.debug("addressTagsOfFormulaEventsOriginalList.toList() size:  [{}]", addressTagsOfFormulaEventsOriginalList.size());
    log.debug("addressTagsOfFormulaEventsOriginalList.toList():{}", Util.prettyPrintAddressTags(addressTagsOfFormulaEventsOriginalList));

    List<AddressTag> addressTagsOfFormulaEvents = addressTagsOfFormulaEventsOriginalList.stream().toList();
    log.debug("addressTagsOfFormulaEvents.toList() size:  [{}]", addressTagsOfFormulaEvents.size());
    log.debug("addressTagsOfFormulaEvents.distinct().toList():{}", Util.prettyPrintAddressTags(addressTagsOfFormulaEvents));

    if (addressTagsOfFormulaEvents.isEmpty()) {
      log.debug("addressTagsOfFormulaEvents was Empty. throwing exception");
      throw new NostrException(
          String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));
    }

    List<FormulaEvent> formulaEvents = addressTagsOfFormulaEvents.stream()
        .map(cacheFormulaEventService::getAddressTagAsFormulaEvent)
        .flatMap(Optional::stream).toList();
    log.debug("cacheFormulaEventService::getAddressTagAsFormulaEvent returned:\n  {}",
        Util.prettyPrintGenericEventRecords(formulaEvents.stream().map(FormulaEvent::getGenericEventRecord).toList()));

    log.debug("addressTagsOfFormulaEvents.size(): [{}], formulaEvents.size(): [{}]",
        addressTagsOfFormulaEvents.size(), formulaEvents.size());

    if (!Objects.equals(addressTagsOfFormulaEvents.size(), formulaEvents.size()))
      throw new NostrException(
          String.format("Unequal count AddressTags vs FormulaEvents:%s\nFormulaEvent:\n%s",
              Util.prettyPrintAddressTags(addressTagsOfFormulaEvents),
              Util.prettyPrintGenericEventRecords(formulaEvents.stream().map(FormulaEvent::getGenericEventRecord).toList())));

    log.debug("formulaEvents size matches addressTag size, return formulaEvents");
    return formulaEvents;
  }

  @Override
  public List<BadgeDefinitionReputationEvent> getExistingReputationDefinitionEvents() {
    return cacheServiceIF.getByKind(
            Kind.BADGE_DEFINITION_EVENT).stream()
        .filter(ger ->
            !Filterable.getTypeSpecificTags(ExternalIdentityTag.class, ger).isEmpty())
        .map(genericEventRecord -> getEvent(genericEventRecord.getId(),
            Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord)
                .findFirst()
                .map(RelayTag::getRelay)
                .map(Relay::getUrl).orElseThrow()))
        .flatMap(Optional::stream).toList();
  }

  //  TODO: needs efficiency refactor
  @Override
  public Optional<BadgeDefinitionReputationEvent> getExistingReputationDefinitionEvent(@NonNull AddressTag addressTag) {
    log.debug("called getExistingReputationDefinitionEvent(AddressTag) with addressTag:\n  {}", addressTag);

    if (!addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
      throw new NostrException(
          String.format("invalid addressTag.getKind(): [%s] for ReputationDefinitionEvent.  must be kind type [%s]", addressTag.getKind(), Kind.BADGE_DEFINITION_EVENT));

    Optional<GenericEventRecord> existingBadgeDefinitionReputationEventGEROptional = cacheDereferenceAddressTagServiceIF.getEvent(addressTag);
    if (existingBadgeDefinitionReputationEventGEROptional.isEmpty())
      throw new NostrException(
          String.format("cacheDereferenceAddressTagServiceIF.getEvent(addressTag) using addressTag:\n  %s\nnot found", addressTag));

    GenericEventRecord existingBadgeDefinitionReputationEventGER = existingBadgeDefinitionReputationEventGEROptional.get();
    log.debug("existingBadgeDefinitionReputationEventGER is:\n  {}", existingBadgeDefinitionReputationEventGER);
    log.debug("existingBadgeDefinitionReputationEventGER createPrettyPrintJson is:\n  {}", existingBadgeDefinitionReputationEventGER.createPrettyPrintJson());

    String relayTagUrl = Filterable.getTypeSpecificTagsStream(RelayTag.class, existingBadgeDefinitionReputationEventGER)
        .findFirst()
        .map(RelayTag::getRelay)
        .map(Relay::getUrl).orElseThrow(() ->
            new NostrException("existingBadgeDefinitionReputationEventGER missing required relayTag"));
    log.debug("relayTagUrl is:\n  {}", relayTagUrl);

    log.debug("calling getEvent(existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl with eventId: [{}], relayUrl: [{}]", existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl);
    Optional<BadgeDefinitionReputationEvent> event = getEvent(existingBadgeDefinitionReputationEventGER.getId(), relayTagUrl);

    if (event.isEmpty()) {
      log.debug("badgeDefinitionReputationEvent.getId()) [%s] not found, throwing exception");
      throw new NostrException(
          String.format("getEvent(badgeDefinitionReputationEvent.getId()) [%s] not found", existingBadgeDefinitionReputationEventGER.getId()));
    }

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = event.get();
    log.debug("... returning found badgeDefinitionReputationEvent:\n {}", badgeDefinitionReputationEvent);
    log.debug("... badgeDefinitionReputationEvent prettyPrintJson:\n {}", badgeDefinitionReputationEvent.createPrettyPrintJson());

//    List<BadgeDefinitionReputationEvent> existingReputationDefinitionEvents = getExistingReputationDefinitionEvents();
//    List<BadgeDefinitionReputationEvent> existingReputationDefinitionEventsFilteredByIdentifierTag = existingReputationDefinitionEvents.stream().filter(event ->
//        event.getIdentifierTag().equals(identifierTag)).toList();
//    List<BadgeDefinitionReputationEvent> existingReputationDefinitionEventsFilteredByExternalIdentifierTag = existingReputationDefinitionEventsFilteredByIdentifierTag.stream().filter(event ->
//        event.getExternalIdentityTag().equals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG)).toList();
//    List<BadgeDefinitionReputationEvent> list = existingReputationDefinitionEventsFilteredByExternalIdentifierTag.stream().filter(event -> event.getPublicKey().equals(publicKey)).distinct().toList();
//    Optional<BadgeDefinitionReputationEvent> first = Optional.of(list.getFirst());
    return Optional.of(badgeDefinitionReputationEvent);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
