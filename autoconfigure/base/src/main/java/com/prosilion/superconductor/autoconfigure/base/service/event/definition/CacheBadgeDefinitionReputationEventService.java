package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
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
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheFormulaEventService cacheFormulaEventService,
     @NonNull CacheKindAddressTagService cacheKindAddressTagService) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.cacheKindAddressTagService = cacheKindAddressTagService;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> materialize(@NonNull EventIF inBadgeDefnRepEvent) {

    GenericEventRecord eventRecord = inBadgeDefnRepEvent.asGenericEventRecord();
    List<FormulaEvent> formulaEvents = getFormulaEvents(eventRecord);

    return
       Optional.of(
          new BadgeDefinitionReputationEvent(
             eventRecord, addressTag ->
             formulaEvents.stream()
                .filter(formulaEvent ->
                   formulaEvent.asAddressableEventAddressTag().equals(addressTag))
                .findFirst()
                .orElseThrow(() ->
                   new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, eventRecord)))));
  }

  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
    List<AddressTag> addressTagsAreFormulaEvents = genericEventRecord.getTypeSpecificTags(AddressTag.class);
    if (addressTagsAreFormulaEvents.isEmpty())
//          TODO: revisit throw -vs- Optional.empty()      
      throw new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));

    List<FormulaEvent> formulaEvents = addressTagsAreFormulaEvents.stream()
       .map(addressTag ->
          cacheFormulaEventService.getBy(
             addressTag.getPublicKey(),
             addressTag.requireIdentifierTag(),
             addressTag.requireRelay())).flatMap(Optional::stream).toList();

    if (!Objects.equals(addressTagsAreFormulaEvents.size(), formulaEvents.size()))
      throw new NostrException(
         String.format("Unequal count AddressTags vs FormulaEvents:%s\nFormulaEvent:\n%s",
            Util.prettyPrintAddressTags(addressTagsAreFormulaEvents),
            Util.prettyPrintGenericEventRecords(formulaEvents.stream().map(FormulaEvent::getGenericEventRecord).toList())));

    return formulaEvents;
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByDirect(@NonNull AddressTag addressTag) {
    return
       cacheKindAddressTagService
          .getByDirect(
             Kind.BADGE_DEFINITION_EVENT,
             addressTag).stream()
          .filter(genericEventRecord ->
             genericEventRecord.findFirstTag(ExternalIdentityTag.class).isPresent()).findFirst()
          .flatMap(genericEventRecord ->
             getEvent(
                genericEventRecord.getId(),
                genericEventRecord.requireFirstTag(RelayTag.class).getRelay()));
  }
}
