package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionReputationEvent> implements CacheBadgeDefinitionReputationEventServiceIF {
  private final CacheFormulaEventService cacheFormulaEventService;
  private final CacheKindAddressTagService cacheKindAddressTagService;

  public CacheBadgeDefinitionReputationEventService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheFormulaEventService cacheFormulaEventService,
     @NonNull CacheKindAddressTagService cacheKindAddressTagService) {
    super(cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.cacheKindAddressTagService = cacheKindAddressTagService;
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    log.debug("... materialize(incomingBadgeDefinitionReputationEvent)...\n{}", incomingBadgeDefinitionReputationEvent.createPrettyPrintJson());

    List<FormulaEvent> formulaEvents = getFormulaEvents(incomingBadgeDefinitionReputationEvent.asGenericEventRecord());
    log.debug("... formulaEvent count: [{}]", formulaEvents.size());
    log.debug("... materialized formulaEvents ...\n  {}", formulaEvents.stream().map(EventIF::asGenericEventRecord).map(GenericEventRecord::createPrettyPrintJson).toList());

    log.debug("formulaEvents as addressTags:");
    formulaEvents.stream().map(AddressableEvent::asAddressableEventAddressTag).map(Util::prettyPrintAddressTags).toList().forEach(log::debug);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       incomingBadgeDefinitionReputationEvent.asGenericEventRecord(), addressTag ->
       formulaEvents.stream().filter(formulaEvent ->
          formulaEvent.asAddressableEventAddressTag().equals(addressTag)).findFirst().orElseThrow(() ->
          new NostrException(
             String.format(NON_EXISTENT_ADDRESS_TAG, incomingBadgeDefinitionReputationEvent))));
    log.debug("returning badgeDefinitionReputationEvent:\n{}", incomingBadgeDefinitionReputationEvent.createPrettyPrintJson());
    return badgeDefinitionReputationEvent;
  }

  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord badgeDefinitionReputationEventGER) {
    log.debug("getFormulaEvents(badgeDefinitionReputationEventGER):\n{}", badgeDefinitionReputationEventGER.createPrettyPrintJson());

    List<AddressTag> addressTagsAreFormulaEvents = badgeDefinitionReputationEventGER.getTypeSpecificTags(AddressTag.class);
    log.debug("addressTagsAreFormulaEventsOriginalList.toList() size:  [{}]", addressTagsAreFormulaEvents.size());
    log.debug("addressTagsAreFormulaEventsOriginalList.toList():\n{}", Util.prettyPrintAddressTags(addressTagsAreFormulaEvents));

    if (addressTagsAreFormulaEvents.isEmpty()) {
      log.debug("addressTagsAreFormulaEvents was Empty. throwing exception");
      throw new NostrException(
         String.format(NON_EXISTENT_ADDRESS_TAG, badgeDefinitionReputationEventGER));
    }

    List<FormulaEvent> formulaEvents = addressTagsAreFormulaEvents.stream()
       .map(addressTag ->
          cacheFormulaEventService.getBy(
             addressTag.getPublicKey(),
             addressTag.requireIdentifierTag(),
             addressTag.requireRelay())).flatMap(Optional::stream).toList();
    log.debug("cacheFormulaEventService.getBy(publicKey, identifierTag) returned:\n  {}",
       formulaEvents.stream().map(EventIF::asGenericEventRecord).map(GenericEventRecord::createPrettyPrintJson).toList());

    log.debug("addressTagsAreFormulaEvents.size(): [{}], formulaEvents.size(): [{}]",
       addressTagsAreFormulaEvents.size(), formulaEvents.size());

    if (!Objects.equals(addressTagsAreFormulaEvents.size(), formulaEvents.size()))
      throw new NostrException(
         String.format("Unequal count AddressTags vs FormulaEvents:%s\nFormulaEvent:\n%s",
            Util.prettyPrintAddressTags(addressTagsAreFormulaEvents),
            Util.prettyPrintGenericEventRecords(formulaEvents.stream().map(FormulaEvent::getGenericEventRecord).toList())));

    log.debug("formulaEvents size matches addressTag size, return formulaEvents");
    return formulaEvents;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(@NonNull AddressTag addressTag) {
    log.debug("... inside getBy(addressTag), value: addressTag:  [{}]", addressTag.toStringPrettyPrint());
    Optional<BadgeDefinitionReputationEvent> foundEvent = super.getBy(addressTag);

    if (foundEvent.isEmpty()) {
      log.debug("... no match found for addressTag:\n  {}", addressTag.toStringPrettyPrint());
      log.debug("... return Optional.empty()");
      return Optional.empty();
    }
    final String addressTagPretty = foundEvent.get().createPrettyPrintJson();
    log.debug("... matching addressTag found:\n  {}", addressTagPretty);

//    TODO: test below inclusion of returnedPubKeyTagPublicKey.equals(inputPubKeyTagPublicKey)
//    log.debug("... checking PubKeyTag match ...");
//    PublicKey returnedPubKeyTagPublicKey = foundEvent.get().requireFirstTag(PubKeyTag.class).getPublicKey();
//    PublicKey inputPubKeyTagPublicKey = pubKeyTag.getPublicKey();
//    if (returnedPubKeyTagPublicKey.equals(inputPubKeyTagPublicKey)) {
//      log.debug("... returned BadgeDefinitionReputationEvent pubKeyTag:  [{}] ...", returnedPubKeyTagPublicKey.toHexString());
//      log.debug("... did not match target pubKeyTag:  [{}] ...", inputPubKeyTagPublicKey);
//      log.debug("... return Optional.empty()");
//      return Optional.empty();
//    }

    log.debug("... PubKeyTag match SKIPPED (needs review), checking for ExternalIdentityTag ...");
    if (foundEvent.get().findFirstTag(ExternalIdentityTag.class).isEmpty()) {
      log.debug("... missing ExternalIdentityTag.  return Optional.empty()");
      return Optional.empty();
    }

    log.debug("... getBy(PubKeyTag, AddressTag) matches all passed, returning BadgeDefinitionReputationEvent:\n  {}", addressTagPretty);
    return foundEvent;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByDirectTag(@NonNull AddressTag addressTag) {
    log.debug("... inside getByDirectTag(addressTag), value: addressTag:  [{}]", addressTag.toStringPrettyPrint());
    List<GenericEventRecord> badgeDefinitionEventGERs =
       cacheKindAddressTagService.getBy(Kind.BADGE_DEFINITION_EVENT, addressTag).stream().filter(ger ->
          ger.findFirstTag(ExternalIdentityTag.class).isPresent()).toList();
    log.debug("cacheKindAddressTagService.getBy(Kind.BADGE_DEFINITION_EVENT, addressTag) returned:\n {}",
       badgeDefinitionEventGERs.stream().map(GenericEventRecord::createPrettyPrintJson));

    List<EventTag> eventTagStream = badgeDefinitionEventGERs.stream().map(ger ->
       new EventTag(ger.getId(), ger.requireRelayTagUrl())).toList();

    List<BadgeDefinitionReputationEvent> badgeDefinitionReputationEvents =
       eventTagStream.stream()
          .map(eventTag ->
             getEvent(eventTag.getIdEvent(), eventTag.requireRecommendedRelayUrl()))
          .flatMap(Optional::stream).toList();

    int size = badgeDefinitionReputationEvents.size();
    if (size > 1) {
      log.debug("11111111111111111111111111111111111");
      log.debug("11111111111111111111111111111111111");
      log.debug("---  WARNING  /  REVISIT / TODO  ---");
      log.debug("cacheKindAddressTagService.getBy(kind, addressTag) returned badgeDefinitionReputationEvents.size() [{}] is > 1", size);
      log.debug("current implementation naively returns first item");
      log.debug("---  WARNING  /  REVISIT / TODO  ---");
      log.debug("11111111111111111111111111111111111");
      log.debug("11111111111111111111111111111111111");
    }

    log.debug("getEvent(eventId, url) returned:\n {}",
       badgeDefinitionReputationEvents.stream().map(EventIF::createPrettyPrintJson));

    return badgeDefinitionReputationEvents.stream().findFirst();
  }
}
