package com.prosilion.superconductor.supplier.local.abstracts;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.supplier.AbstractBaseBadgeAwardReputationEventMessageListIT;
import com.prosilion.superconductor.util.EventAttributesMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.prosilion.superconductor.BaseCacheFollowSetsEventServiceIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBadgeAwardReputationEventMessageSupplierLocalListIT extends AbstractBaseBadgeAwardReputationEventMessageListIT {
  CacheServiceIF cacheServiceIF;

  protected AbstractBadgeAwardReputationEventMessageSupplierLocalListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl,
     CacheServiceIF cacheServiceIF) {
    super(superconductorInstanceIdentity, definitionEventRelayUrl, awardEventRelayUrl, cacheServiceIF);
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  protected Supplier<List<GenericEventRecord>> getAllFxn() {
    return () -> cacheServiceIF.getAll();
  }

  @Override
  protected Supplier<List<GenericEventRecord>> getAllDeletedFxn() {
    return () -> cacheServiceIF.getAllIncludingDeleted();
  }

  @Override
  protected String getDefinitionEventRelayUrl() {
    return definitionEventRelayUrl;
  }

  @Override
  protected Relay getAwardEventRelay() {
    return new Relay(awardEventRelayUrl);
  }

  protected void validateSetupCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents() {
    List<EventIF> sanityCheckReturnedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned BadgeDefinitionEvents:");
    log.debug("  {}", sanityCheckReturnedBadgeDefinitionEvents);

    Set<String> sanityCheckCurationSetsBadgeDefinitionEventIds =
       sanityCheckReturnedBadgeDefinitionEvents.stream()
          .map(event -> event.requireFirstTag(EventTag.class))
          .map(EventTag::getEventId)
          .collect(Collectors.toSet());

    Predicate<String> contains = EventAttributesMap.asEventList(this.badgeDefinitionGenericEventList).stream().map(EventIF::getId).toList()::contains;

    assertTrue(sanityCheckCurationSetsBadgeDefinitionEventIds.stream().anyMatch(contains));
  }

  protected List<EventIF> getReceivedUpvoteCuratedEventIF(BaseEvent event, List<BaseMessage> baseMessages) {
    log.debug("retrieved superconductor events:");
    List<EventIF> receivedEventIFs = getGenericEvents(baseMessages);
    receivedEventIFs.stream().map(EventIF::createPrettyPrintJson).forEach(log::debug);

//    assertTrue(receivedEventIFs.stream()
//       .map(eventIF -> eventIF.requireFirstTag(EventTag.class).getEventId())
//       .anyMatch(event.getId()::contains));

    assertTrue(receivedEventIFs.stream().map(eventIF ->
       eventIF.requireFirstTag(PubKeyTag.class).getPublicKey()).anyMatch(event.requireFirstTag(PubKeyTag.class).getPublicKey()::equals));
    return receivedEventIFs;
  }
  
  @Override
  protected void validateResidualDbEventCounts() {
    assertEquals(9, getEventCountByKindIncludesDeletedEvents(Kind.BADGE_AWARD_EVENT));
    assertEquals(9, getEventCountByKindIncludesDeletedEvents(Kind.CURATION_SETS_BADGE_AWARD_EVENT));
    assertEquals(9, getEventCountByKindIncludesDeletedEvents(Kind.FOLLOW_SETS));
    assertEquals(16, getEventCountByKindIncludesDeletedEvents(Kind.BADGE_SETS_EVENT));
  }
}
