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
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;

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
                addressTag.getIdentifierTag(),
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
  public Optional<BadgeDefinitionReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    log.debug("... inside getBy(pubKeyTag, addressTag), values:\npubKeyTag:  [{}],\naddressTag:  [{}]",
        pubKeyTag, addressTag.toStringPrettyPrint());
    Optional<BadgeDefinitionReputationEvent> byAddressTag = getBy(addressTag);

    if (byAddressTag.isEmpty())
      return Optional.empty();

    log.debug("... getBy returned:\n  {}", byAddressTag.get().createPrettyPrintJson());
    String dbTag = byAddressTag.get().requireFirstTag(PubKeyTag.class).getPublicKey().toHexString();
    String paramTag = pubKeyTag.getPublicKey().toHexString();
    log.debug("do pubkeyTags match?\n [{}] -vs- [{}]\nequals?  {}", dbTag, paramTag,
        Boolean.valueOf(dbTag.equals(paramTag)).toString().toUpperCase());

    return byAddressTag
//        .filter(event ->
//            event.findFirstTag(PubKeyTag.class)
//                .map(PubKeyTag::getPublicKey).stream()
//                .anyMatch(pubKeyTag.getPublicKey()::equals)).stream().findFirst()
        ;
  }

  @Override
  public List<BadgeDefinitionReputationEvent> getByDirectTag(@NonNull AddressTag addressTag) {
    List<GenericEventRecord> badgeDefinitionEventGERs =
        cacheKindAddressTagService.getBy(Kind.BADGE_DEFINITION_EVENT, addressTag).stream().filter(ger ->
            ger.findFirstTag(ExternalIdentityTag.class).isPresent()).toList();
    log.debug("cacheKindAddressTagService.getBy(Kind.BADGE_DEFINITION_EVENT, addressTag) returned:\n {}",
        badgeDefinitionEventGERs.stream().map(GenericEventRecord::createPrettyPrintJson));

    List<EventTag> eventTagStream = badgeDefinitionEventGERs.stream().map(ger ->
        new EventTag(ger.getId(), ger.getRelayTagUrl())).toList();

    List<BadgeDefinitionReputationEvent> badgeDefinitionReputationEvents =
        eventTagStream.stream()
            .map(eventTag ->
                getEvent(eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl()))
            .flatMap(Optional::stream).toList();

    log.debug("getEvent(eventId, url) returned:\n {}",
        badgeDefinitionReputationEvents.stream().map(EventIF::createPrettyPrintJson));

    return badgeDefinitionReputationEvents;
  }
}
