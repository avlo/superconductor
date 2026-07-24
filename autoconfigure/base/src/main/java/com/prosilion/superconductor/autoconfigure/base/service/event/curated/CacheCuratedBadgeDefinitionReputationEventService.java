package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

public class CacheCuratedBadgeDefinitionReputationEventService extends CacheCuratedEventService<BadgeDefinitionReputationEvent> implements CacheCuratedBadgeDefinitionReputationEventServiceIF {
  private final Identity instanceIdentity;
  private final CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;

  public CacheCuratedBadgeDefinitionReputationEventService(
     @NonNull Identity instanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF) {
    super(cacheServiceIF);
    this.instanceIdentity = instanceIdentity;
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.cacheBadgeDefinitionReputationEventServiceIF = cacheBadgeDefinitionReputationEventServiceIF;
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

  private List<FormulaEvent> getFormulaEvents(GenericEventRecord genericEventRecord) {
    List<AddressTag> addressTagsAreFormulaEvents = genericEventRecord.getTypeSpecificTags(AddressTag.class);
    if (addressTagsAreFormulaEvents.isEmpty())
//          TODO: revisit throw -vs- Optional.empty()      
      throw new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord));

    List<FormulaEvent> formulaEvents = addressTagsAreFormulaEvents.stream()
       .map(addressTag ->
          cacheCuratedFormulaEventServiceIF.getBy(
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
  public Optional<BadgeDefinitionReputationEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super.getEvent(eventId, relay)
       .or(() -> cacheBadgeDefinitionReputationEventServiceIF.getEvent(eventId, relay)
          .flatMap(this::materialize));
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByDirect(@NonNull AddressTag addressTag) {
    return
       cacheServiceIF.getEventsByKindAndAddressTag(getKind(), addressTag)
          .stream().findFirst().flatMap(this::materialize)
          .or(() ->
             cacheBadgeDefinitionReputationEventServiceIF.getByDirect(addressTag));
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getByExpanded(AddressTag addressTag) {
    return cacheServiceIF.getEventByKindAndAuthorPublicKeyAndIdentifierTag(
          addressTag.getKind(),
          addressTag.getPublicKey(),
          addressTag.getIdentifierTag()).stream()
       .findFirst().flatMap(this::materialize)
       .or(() ->
          cacheBadgeDefinitionReputationEventServiceIF.getByExpanded(addressTag));
  }

  @Override
  public Optional<BadgeDefinitionReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag).stream()
       .findFirst().flatMap(this::materialize)
       .or(() ->
          cacheBadgeDefinitionReputationEventServiceIF.getBy(pubKeyTag, identifierTag));
  }

  @Override
  public Kind getKind() {
    return Kind.REFERENCED_SET;
  }
}
