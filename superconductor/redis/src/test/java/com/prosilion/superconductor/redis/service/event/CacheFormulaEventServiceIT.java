package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheDereferenceEventTagService.INVALID_REMOTE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheFormulaEventServiceIT {
  public static final String TEST_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final String TEST_BADGE_DEFINITION_VOTE = "TEST_BADGE_DEFINITION_VOTE";
  public static final String INVALID_URL = "ws://localhost:1111";

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(TEST_BADGE_DEFINITION_VOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public final String PLUS_ONE_FORMULA = "+1";
  public final String MINUS_ONE_FORMULA = "-1";

  private final BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  private final BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent;

  final FormulaEvent formulaEventUpvote;
  final FormulaEvent formulaEventDownvote;

  private final CacheFormulaEventService cacheFormulaEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  public CacheFormulaEventServiceIT(
      @Value("${superconductor.relay.url}") String relayUri,
      @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
      @NonNull @Qualifier("cacheFormulaEventService") CacheFormulaEventService cacheFormulaEventService) throws ParseException {
    this.eventServiceIF = eventServiceIF;
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.relay = new Relay(relayUri);

    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
    this.formulaEventUpvote = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);

    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA);
    this.formulaEventDownvote = new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);
  }

  @Test
  public void testSaveFormulae() throws ParseException, JsonProcessingException {
    eventServiceIF.processIncomingEvent(new EventMessage(awardUpvoteDefinitionEvent));
    eventServiceIF.processIncomingEvent(new EventMessage(awardDownvoteDefinitionEvent));

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvote));
    FormulaEvent dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay.getUrl()).orElseThrow();
    assertEquals(formulaEventUpvote, dbPlusOneFormulaEvent);
    assertEquals(PLUS_ONE_FORMULA, dbPlusOneFormulaEvent.getContent());
    assertEquals(TEST_UNIT_UPVOTE, dbPlusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvote));
    FormulaEvent dbMinusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay.getUrl()).orElseThrow();
    assertEquals(formulaEventDownvote, dbMinusOneFormulaEvent);
    assertEquals(MINUS_ONE_FORMULA, dbMinusOneFormulaEvent.getContent());
    assertEquals(TEST_BADGE_DEFINITION_VOTE, dbMinusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvote));
    dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay.getUrl()).orElseThrow();
    assertEquals(formulaEventUpvote, dbPlusOneFormulaEvent);
    assertEquals(PLUS_ONE_FORMULA, dbPlusOneFormulaEvent.getContent());
    assertEquals(TEST_UNIT_UPVOTE, dbPlusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvote));
    dbMinusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay.getUrl()).orElseThrow();
    assertEquals(formulaEventDownvote, dbMinusOneFormulaEvent);
    assertEquals(MINUS_ONE_FORMULA, dbMinusOneFormulaEvent.getContent());
    assertEquals(TEST_BADGE_DEFINITION_VOTE, dbMinusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    FormulaEvent formulaEventUpvoteIdentical = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, "+1");
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvoteIdentical));
    dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay.getUrl()).orElseThrow();
    assertEquals(PLUS_ONE_FORMULA, formulaEventUpvoteIdentical.getContent());
    assertEquals(TEST_UNIT_UPVOTE, formulaEventUpvoteIdentical.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    FormulaEvent formulaEventDownvoteIdentical = new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, "-1");
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvoteIdentical));
    dbMinusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay.getUrl()).orElseThrow();
    assertEquals(MINUS_ONE_FORMULA, formulaEventDownvoteIdentical.getContent());
    assertEquals(TEST_BADGE_DEFINITION_VOTE, formulaEventDownvoteIdentical.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    FormulaEvent formulaEventUpvoteMisMatch = new FormulaEvent(identity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, "+2");
    assertThrows(NostrException.class, () -> eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvoteMisMatch)));
    EventTag eventTag = new EventTag(formulaEventUpvoteMisMatch.getId(), null);
    assertEquals(
        String.format(INVALID_REMOTE_URL, eventTag, eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl()),
        assertThrows(NostrException.class, () -> cacheFormulaEventService.getEvent(eventTag.getIdEvent(), eventTag.getRecommendedRelayUrl()))
            .getMessage());

    FormulaEvent formulaEventDownvoteMisMatch = new FormulaEvent(identity, downvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, "-2");
    assertThrows(NostrException.class, () -> eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvoteMisMatch)));
    EventTag eventTag2 = new EventTag(formulaEventDownvoteMisMatch.getId(), null);
    assertEquals(
        String.format(INVALID_REMOTE_URL, eventTag2, eventTag2.getIdEvent(), eventTag2.getRecommendedRelayUrl()),
        assertThrows(NostrException.class, () -> cacheFormulaEventService.getEvent(eventTag2.getIdEvent(), eventTag2.getRecommendedRelayUrl())).getMessage());
  }
}
