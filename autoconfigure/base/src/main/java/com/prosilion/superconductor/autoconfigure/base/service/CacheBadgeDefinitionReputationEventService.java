package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionReputationEventService extends AbstractCacheEventTagBaseEventService {
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull @Qualifier("cacheFormulaEventService") CacheEventTagBaseEventServiceIF cacheFormulaEventService) {
    super(cacheServiceIF);
    this.cacheFormulaEventService = (CacheFormulaEventService) cacheFormulaEventService;
  }

  @Override
  public EventTagsMappedEventsIF populate(
      GenericEventRecord badgeDefinitionReputationEvent,
      List<GenericEventRecord> unpopulatedFormulaEvents) {

    List<FormulaEvent> populatedFormulaEvents = unpopulatedFormulaEvents.stream()
        .map(event ->
            cacheFormulaEventService.populate(
                event,
                getList(event))).toList();

    Function<EventTag, FormulaEvent> fxn = eventTag ->
        populatedFormulaEvents.stream().filter(genericEventRecord ->
                genericEventRecord.getId().equals(eventTag.getIdEvent()))
            .findFirst().orElseThrow();

    return createEventGivenMappedEventTagEvents(
        badgeDefinitionReputationEvent,
        BadgeDefinitionReputationEvent.class,
        fxn);
  }

  public Optional<BadgeDefinitionReputationEvent> getBadgeDefinitionReputationEvent(@NonNull String eventId) {
    return (Optional<BadgeDefinitionReputationEvent>) super.getEventByEventId(eventId);
  }

  public List<BadgeDefinitionReputationEvent> getBadgeDefinitionReputationEvents(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull AddressTag addressTag) {
    return (List<BadgeDefinitionReputationEvent>) super.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag);
  }

  public List<BadgeDefinitionReputationEvent> getBadgeDefinitionReputationEvents(Kind kind, PubKeyTag referencedPubkeyTag, IdentifierTag identifierTag) {
    return (List<BadgeDefinitionReputationEvent>) super.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencedPubkeyTag.getPublicKey(), identifierTag);
  }

  public List<BadgeDefinitionReputationEvent> getBadgeDefinitionReputationEvents(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    return (List<BadgeDefinitionReputationEvent>) super.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
