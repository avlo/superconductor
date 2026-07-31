package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

public class CacheBadgeDefinitionReputationEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionReputationEvent> implements CacheBadgeDefinitionReputationEventServiceIF {
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;

  public CacheBadgeDefinitionReputationEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF,
     @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF) {
    super(cacheServiceIF, cacheReferenceEventTagServiceIF, cacheReferenceAddressTagServiceIF);
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
  }

  private boolean supports(@NonNull GenericEventRecord eventRecord) {
    return eventRecord.findFirstTag(ExternalIdentityTag.class).isPresent();
  }

  @Override
  protected BadgeDefinitionReputationEvent createBadgeDefinitionEvent(@NonNull GenericEventRecord eventRecord) {
    List<CuratedFormulaEvent> curatedFormulaEvents = getCuratedFormulaEvents(eventRecord);

    return new BadgeDefinitionReputationEvent(
       eventRecord, addressTag ->
       curatedFormulaEvents.stream()
          .filter(curatedFormulaEvent ->
             curatedFormulaEvent.asAddressableEventAddressTag().equals(addressTag))
          .findFirst()
          .orElseThrow(() ->
             new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, eventRecord))));
  }

  private List<CuratedFormulaEvent> getCuratedFormulaEvents(@NonNull GenericEventRecord cacheBadgeDefinitionReputationEvent) {
    List<AddressTag> addressTagsAreCuratedFormulaEvents = cacheBadgeDefinitionReputationEvent.getTypeSpecificTags(AddressTag.class);
    if (addressTagsAreCuratedFormulaEvents.isEmpty())
//          TODO: revisit throw -vs- Optional.empty()      
      throw new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, cacheBadgeDefinitionReputationEvent));

    List<CuratedFormulaEvent> curatedFormulaEvents = addressTagsAreCuratedFormulaEvents.stream()
       .map(addressTag ->
          cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
             addressTag.getPublicKey(),
             addressTag.getIdentifierTag())).flatMap(Optional::stream).toList();

    if (!Objects.equals(addressTagsAreCuratedFormulaEvents.size(), curatedFormulaEvents.size()))
      throw new NostrException(
         String.format("Unequal count AddressTags vs FormulaEvents:%s\nFormulaEvent:\n%s",
            addressTagsAreCuratedFormulaEvents.size(),
            curatedFormulaEvents.size()));

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
