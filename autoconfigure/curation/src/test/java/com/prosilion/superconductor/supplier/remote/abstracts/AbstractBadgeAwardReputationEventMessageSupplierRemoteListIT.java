package com.prosilion.superconductor.supplier.remote.abstracts;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBadgeAwardReputationEventMessageSupplierRemoteListIT extends AbstractBaseBadgeAwardReputationEventMessageListIT {
  private static final Relay badgeAwardEventRelay = new Relay("ws://superconductor-app-three:5555");
  private static final Relay badgeDefinitionEventRelay = new Relay("ws://superconductor-app-two:5555");

  protected AbstractBadgeAwardReputationEventMessageSupplierRemoteListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) {
    super(superconductorInstanceIdentity, definitionEventRelayUrl, awardEventRelayUrl);
  }

  @Override
  protected Supplier<List<GenericEventRecord>> getAllFxn() {
    ReqMessage reqMessage = new ReqMessage(
       generateRandomHex64String(),
       new Filters(kindFilters));

    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(reqMessage, awardEventRelayUrl);
    List<GenericEventRecord> genericEvents = getGenericEvents(baseMessages).stream().map(EventIF::asGenericEventRecord).toList();
    return () -> genericEvents;
  }

  @Override
  protected Supplier<List<GenericEventRecord>> getAllDeletedFxn() {
    ReqMessage reqMessage = new ReqMessage(
       generateRandomHex64String(),
       new Filters(
          new KindFilter(Kind.DELETION)));

    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(reqMessage, awardEventRelayUrl);
    List<GenericEventRecord> genericEvents = getGenericEvents(baseMessages).stream().map(EventIF::asGenericEventRecord).toList();
    return () -> genericEvents;
  }

  @Override
  protected String getDefinitionEventRelayUrl() {
    return badgeDefinitionEventRelay.getUrl();
  }

  @Override
  protected Relay getAwardEventRelay() {
    return badgeAwardEventRelay;
  }

  protected void validateSetupCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents() {
    List<EventIF> sanityCheckReturnedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(new KindFilter(Kind.BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned BadgeDefinitionEvents:");
    log.debug("  {}", sanityCheckReturnedBadgeDefinitionEvents);

    Set<String> sanityCheckCurationSetsBadgeDefinitionEventIds =
       sanityCheckReturnedBadgeDefinitionEvents.stream()
          .map(EventIF::getId)
          .collect(Collectors.toSet());

    Predicate<String> contains = EventAttributesMap.asEventList(this.badgeDefinitionGenericEventList).stream().map(EventIF::getId).toList()::contains;

    assertTrue(sanityCheckCurationSetsBadgeDefinitionEventIds.stream().anyMatch(contains));
  }

  @Override
  protected void validateResidualDbEventCounts() {
  }
}
