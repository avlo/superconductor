package com.prosilion.superconductor.supplier.remote.abstracts;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.supplier.AbstractBaseCacheBadgeAwardCanonicalEventMessageListIT;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.prosilion.nostr.util.Util.generateRandomHex64String;
import static com.prosilion.superconductor.BaseCacheFollowSetsEventServiceIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractCacheBadgeAwardCanonicalEventMessageSupplierRemoteListIT extends AbstractBaseCacheBadgeAwardCanonicalEventMessageListIT {

  private static final Relay badgeAwardEventRelay = new Relay("ws://superconductor-app-three:5555");
  private static final Relay badgeDefinitionEventRelay = new Relay("ws://superconductor-app-two:5555");

  protected AbstractCacheBadgeAwardCanonicalEventMessageSupplierRemoteListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl,
     CacheServiceIF cacheServiceIF) throws NostrException {
    super(superconductorInstanceIdentity, definitionEventRelayUrl, awardEventRelayUrl, cacheServiceIF);
  }

  @Override
  protected void validatePersistedCurationSetsBadgeDefinitionEvents(List<BadgeAwardCanonicalEvent> badgeAwardUpvoteEvents) {
    List<EventIF> sanityCheckReturnedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned BadgeDefinitionEvents:");
    log.debug("  {}", sanityCheckReturnedBadgeDefinitionEvents.stream().map(EventIF::createPrettyPrintJson).collect(Collectors.joining(",\n")));

    Set<String> sanityCheckBadgeDefinitionEventIds = sanityCheckReturnedBadgeDefinitionEvents.stream().map(EventIF::getId).collect(Collectors.toSet());

    assertTrue(sanityCheckBadgeDefinitionEventIds.stream().anyMatch(
       badgeAwardGenericEventList.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
          .map(BaseEvent::getId).toList()::contains));

    badgeAwardUpvoteEvents.forEach(badgeAwardUpvoteEvent -> {
      EventMessage eventMessageBadgeAwardUpvoteEvent = new EventMessage(badgeAwardUpvoteEvent);
      Boolean flag = new NostrEventPublisher(awardEventRelayUrl).send(eventMessageBadgeAwardUpvoteEvent, Duration.ofSeconds(10)).getFlag();
      assertTrue(flag);
    });
  }

  @Override
  protected List<BadgeAwardCanonicalEvent> createBadgeAwardEventList() {
    return List.of(
       create_AwardEventWithRelayTag_DefinitionEventWithRelayTag()
//       ,
//       create_AwardEventWithRelayTag_DefinitionEventWithoutRelayTag()
//       ,
//       create_AwardEventWithoutRelayTag_DefinitionEventWithRelayTag()

// below test commented out due to "fromRelay" parameter binding to localhost:5553, which is neither accessible nor services requests during IT
//     create_AwardEventWithoutRelayTag_DefinitionEventWithoutRelayTag()
    );
  }

  protected BadgeAwardCanonicalEvent create_AwardEventWithRelayTag_DefinitionEventWithRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_1");
    return new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag,
          badgeDefinitionEventRelay),
       badgeAwardEventRelay);
  }

  protected BadgeAwardCanonicalEvent create_AwardEventWithRelayTag_DefinitionEventWithoutRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_2");
    return new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag),
       badgeDefinitionEventRelay); // <---- if present (for BadgeAwardEvent), SC should implicitly use it iff BadgeDefinitionGenericEvent hasn't specified a relay   
  }

  protected BadgeAwardCanonicalEvent create_AwardEventWithoutRelayTag_DefinitionEventWithRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_3");
    return new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag,
          badgeDefinitionEventRelay));
  }


  protected BadgeAwardCanonicalEvent create_AwardEventWithoutRelayTag_DefinitionEventWithoutRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_4");
    return new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag));
  }
}
