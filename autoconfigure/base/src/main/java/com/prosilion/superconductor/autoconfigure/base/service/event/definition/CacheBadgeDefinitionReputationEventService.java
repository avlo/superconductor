package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

public class CacheBadgeDefinitionReputationEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionReputationEvent> implements CacheBadgeDefinitionReputationEventServiceIF {
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  public CacheBadgeDefinitionReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  private boolean supports(@NonNull GenericEventRecord eventRecord) {
    return eventRecord.findFirstTag(ExternalIdentityTag.class).isPresent();
  }

  @Override
  protected BadgeDefinitionReputationEvent createBadgeDefinitionEvent(@NonNull GenericEventRecord eventRecord) {
    List<FormulaEvent> formulaEvents = getFormulaEvents(eventRecord);

    return new BadgeDefinitionReputationEvent(
       eventRecord, addressTag ->
       formulaEvents.stream()
          .filter(formulaEvent ->
             formulaEvent.asAddressableEventAddressTag().equals(addressTag))
          .findFirst()
          .orElseThrow(() ->
             new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, eventRecord))));
  }

  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord genericEventRecord) {
    List<AddressTag> addressTagsAreFormulaEvents = genericEventRecord.getTypeSpecificTags(AddressTag.class);
    if (addressTagsAreFormulaEvents.isEmpty())
//          TODO: revisit throw -vs- Optional.empty()      
      throw new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));

    List<FormulaEvent> formulaEvents = addressTagsAreFormulaEvents.stream()
       .map(addressTag ->
          cacheFormulaEventServiceIF.getBy(
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
