package com.prosilion.superconductor.autoconfigure.curation.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionAbstractEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeDefinitionReputationEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionReputationEvent> implements CacheBadgeDefinitionReputationEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "cacheBadgeDefinitionReputationEventGER:%n  %s%nis missing required AddressTag: %s";
  
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;

  public CacheBadgeDefinitionReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
  }

  private boolean supports(@NonNull GenericEventRecord eventRecord) {
    return eventRecord.findFirstTag(ExternalIdentityTag.class).isPresent();
  }

  @Override
  public List<BadgeDefinitionReputationEvent> findByMatching(CuratedBadgeAwardCanonicalEvent curatedBadgeAwardCanonicalEvent) {
    return
       cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
             curatedBadgeAwardCanonicalEvent.getPublicKey(),
             cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(
                curatedBadgeAwardCanonicalEvent.getIdentifierTag().getUuid(), // hash(a3_0009Tag), aka ["a", "30009:BDG_DEF_UP_CREATOR_PK:BDG_DEF_UNIT_UP"]
                curatedBadgeAwardCanonicalEvent.requireFirstTag(RelayTag.class).getRelay()).orElseThrow().getIdentifierTag())
          .map(CuratedFormulaEvent::asAddressableEventAddressTag).map(this::getByDirect).stream().flatMap(Optional::stream).toList();
  }

  @Override
  protected BadgeDefinitionReputationEvent createBadgeDefinitionEvent(
     @NonNull GenericEventRecord cacheBadgeDefinitionReputationEventGER) {

    log.debug("inside createBadgeDefinitionEvent(GenericEventRecord):\n  {}",
       cacheBadgeDefinitionReputationEventGER.createPrettyPrintJson());
    List<CuratedFormulaEvent> curatedFormulaEvents = getCuratedFormulaEvents(cacheBadgeDefinitionReputationEventGER);

    log.debug("received curatedFormulaEvents:\n  {}",
       curatedFormulaEvents.stream().map(CuratedFormulaEvent::createPrettyPrintJson).collect(Collectors.joining(",\n  ")));

    Map<AddressTag, CuratedFormulaEvent> addressTagCuratedFormulaEventMap = curatedFormulaEvents.stream()
       .collect(Collectors.toMap(
          CuratedFormulaEvent::asAddressableEventAddressTag,
          Function.identity()));

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       cacheBadgeDefinitionReputationEventGER, addressTag ->
       Optional.ofNullable(addressTagCuratedFormulaEventMap.get(addressTag))
          .orElseThrow(() ->
             new NostrException(
                String.format(
                   NON_EXISTENT_ADDRESS_TAG,
                   cacheBadgeDefinitionReputationEventGER.createPrettyPrintJson(),
                   addressTag))));
    log.debug("createBadgeDefinitionEvent successfully returning BadgeDefinitionReputationEvent:\n  {}",
       badgeDefinitionReputationEvent.createPrettyPrintJson());
    return badgeDefinitionReputationEvent;
  }

  private List<CuratedFormulaEvent> getCuratedFormulaEvents(@NonNull GenericEventRecord cacheBadgeDefinitionReputationEventGER) {
    log.debug("inside getCuratedFormulaEvents(GenericEventRecord):\n  {}",
       cacheBadgeDefinitionReputationEventGER.createPrettyPrintJson());

    List<AddressTag> addressTagsAreCuratedFormulaEvents = cacheBadgeDefinitionReputationEventGER.getTypeSpecificTags(AddressTag.class);

    if (addressTagsAreCuratedFormulaEvents.isEmpty())
//          TODO: revisit throw -vs- Optional.empty()      
      throw new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, cacheBadgeDefinitionReputationEventGER));

    List<CuratedFormulaEvent> curatedFormulaEvents = addressTagsAreCuratedFormulaEvents.stream()
       .map(addressTag ->
       {
         log.debug("calling cacheCuratedFormulaEventServiceIF.getByExpanded(addressTag): {}", addressTag);
         Optional<CuratedFormulaEvent> byAuthorAndIdentifierTag =
            cacheCuratedFormulaEventServiceIF.getByExpanded(addressTag);
         log.debug("returned CuratedFormulaEvent:\n  {}",
            byAuthorAndIdentifierTag.map(CuratedFormulaEvent::createPrettyPrintJson).orElse("*** EMPTY OPTIONAL WILL YIELD EXCEPTION ***"));
         return byAuthorAndIdentifierTag;
       }).flatMap(Optional::stream).toList();

    if (!Objects.equals(addressTagsAreCuratedFormulaEvents.size(), curatedFormulaEvents.size()))
      throw new NostrException(
         String.format("Unequal count AddressTags: [%d] -vs- FormulaEvents: [%d]",
            addressTagsAreCuratedFormulaEvents.size(),
            curatedFormulaEvents.size()));

    log.debug("getCuratedFormulaEvents() successfully returning curatedFormulaEvents:\n  {}", curatedFormulaEvents.stream().map(CuratedFormulaEvent::createPrettyPrintJson).collect(Collectors.joining(",\n ")));
    return curatedFormulaEvents;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByDirect(@NonNull AddressTag addressTag) {
    return cacheKindAddressTagServiceIF
       .getByDirect(getKind(), addressTag).stream()
       .filter(this::supports)
       .findFirst()
       .flatMap(this::materialize);
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(
     @NonNull PubKeyTag pubKeyTag,
     @NonNull IdentifierTag identifierTag) {
    return findFirstByPubKeyAndIdentifier(pubKeyTag, identifierTag, this::supports)
       .flatMap(this::materialize);
  }
}
