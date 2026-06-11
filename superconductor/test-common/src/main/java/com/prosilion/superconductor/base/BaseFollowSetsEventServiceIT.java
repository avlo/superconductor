package com.prosilion.superconductor.base;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.time.Duration;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseFollowSetsEventServiceIT {
  public static final String REPUTATION = "TEST_REPUTATION";
  public static final String AWARD_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  public static final String FORMULA_UNIT_DOWNVOTE = "FORMULA_UNIT_DOWNVOTE";

  public static final String PLUS_ONE_FORMULA = "+1";

  protected final IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
  protected final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);
  protected final IdentifierTag formulaUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);
  protected final IdentifierTag formulaDownvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_DOWNVOTE);

  private final Identity identity = Identity.generateRandomIdentity();

  protected final Identity submitter =
//     Identity.generateRandomIdentity();
     Identity.create("aaa4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity recipient =
//     Identity.generateRandomIdentity();
     Identity.create("ccc4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity formulaCreator =
//     Identity.generateRandomIdentity();
     Identity.create("ddd4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity repDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("eee4585483196998204846989544737603523651520600328805626488477202");

  private final BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula;
  private final BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  Duration requestTimeoutDuration;

  public BaseFollowSetsEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventServiceIF cacheFollowSetsEventService,
     Duration requestTimeoutDuration) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.requestTimeoutDuration = requestTimeoutDuration;
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;
    this.relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    FormulaEvent plusOneFormulaEvent = new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
    eventServiceIF.processIncomingEvent(new EventMessage(plusOneFormulaEvent));

    this.badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       submitter.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       plusOneFormulaEvent);
    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula));

    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       relay,
       badgeDefinitionReputationEventPlusOneFormula);
    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardUpvoteEvent));
  }

  @Test
  public void testSaveBadgeAwardReputationEventUpvote() {
    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       identity,
       badgeDefinitionReputationEventPlusOneFormula,
       relay,
       List.of(badgeAwardUpvoteEvent));

    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent));

    FollowSetsEvent dbFollowSetsEventByEventId = cacheFollowSetsEventService.getEvent(followSetsEvent.getId(), relay.getUrl()).orElseThrow();
    assertEquals(followSetsEvent, dbFollowSetsEventByEventId);

    List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardAbstractEvents = dbFollowSetsEventByEventId.getBadgeAwardGenericEvents();
    assertTrue(badgeAwardAbstractEvents.contains(badgeAwardUpvoteEvent));

    PublicKey matchPubkey = dbFollowSetsEventByEventId.getAwardRecipientPulicKey();
    assertEquals(matchPubkey, recipient.getPublicKey());

    assertEquals(followSetsEvent.getAddressTag(), dbFollowSetsEventByEventId.getAddressTag());
    assertEquals(followSetsEvent.getEventTags(), dbFollowSetsEventByEventId.getEventTags());
    assertEquals(followSetsEvent.getBadgeAwardGenericEvents(), dbFollowSetsEventByEventId.getBadgeAwardGenericEvents());
    assertEquals(followSetsEvent.getBadgeDefinitionReputationEvent(), dbFollowSetsEventByEventId.getBadgeDefinitionReputationEvent());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService()
          .send(
             new ReqMessage(
                Factory.generateRandomHex64String(),
                new Filters(
                   new KindFilter(
                      Kind.FOLLOW_SETS))),
             relay.getUrl()));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().map(EventIF::getKind).toList().contains(Kind.FOLLOW_SETS));

    assertTrue(badgeAwardAbstractEvents.stream()
       .map(BadgeAwardGenericEvent::getBadgeDefinitionEvent)
       .anyMatch(badgeDefinitionReputationEventPlusOneFormula::equals));
  }
}
