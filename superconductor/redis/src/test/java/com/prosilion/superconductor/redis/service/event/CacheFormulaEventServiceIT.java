package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheFormulaEventServiceIT extends BaseIntegrationTestFixtures {
  private final BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  private final BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent;

  final FormulaEvent formulaEventUpvote;
  final FormulaEvent formulaEventDownvote;

  private final CacheFormulaEventService cacheFormulaEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  @Autowired
  public CacheFormulaEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFormulaEventService") CacheFormulaEventService cacheFormulaEventService) throws ParseException {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.relay = new Relay(relayUri);

    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(repDefnCreator, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);
    this.formulaEventUpvote = new FormulaEvent(formulaCreator, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);

    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(repDefnCreator, downvoteIdentifierTag, MINUS_ONE_FORMULA, relay);
    this.formulaEventDownvote = new FormulaEvent(formulaCreator, downvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);

    cacheServiceIF.save(awardUpvoteDefinitionEvent);
    cacheServiceIF.save(awardDownvoteDefinitionEvent);
  }

  @Test
  public void testSaveFormulae() throws ParseException {
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvote), relay);
    FormulaEvent dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay).orElseThrow();
    assertEquals(formulaEventUpvote, dbPlusOneFormulaEvent);
    assertEquals(PLUS_ONE_FORMULA, dbPlusOneFormulaEvent.getContent());
    assertEquals(AWARD_UNIT_UPVOTE, dbPlusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag().getUuid());

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvote), relay);
    FormulaEvent dbMinusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay).orElseThrow();
    assertEquals(formulaEventDownvote, dbMinusOneFormulaEvent);
    assertEquals(MINUS_ONE_FORMULA, dbMinusOneFormulaEvent.getContent());
    assertEquals(downvoteIdentifierTag, dbMinusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag());

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvote), relay);
    dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay).orElseThrow();
    assertEquals(formulaEventUpvote, dbPlusOneFormulaEvent);
    assertEquals(PLUS_ONE_FORMULA, dbPlusOneFormulaEvent.getContent());
    assertEquals(upvoteIdentifierTag, dbPlusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag());

    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvote), relay);
    dbMinusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay).orElseThrow();
    assertEquals(formulaEventDownvote, dbMinusOneFormulaEvent);
    assertEquals(MINUS_ONE_FORMULA, dbMinusOneFormulaEvent.getContent());
    assertEquals(downvoteIdentifierTag, dbMinusOneFormulaEvent.getBadgeDefinitionGenericEvent().getIdentifierTag());

    FormulaEvent formulaEventUpvoteIdentical = new FormulaEvent(formulaCreator, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, "+1");
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvoteIdentical), relay);
    dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay).orElseThrow();
    assertEquals(PLUS_ONE_FORMULA, formulaEventUpvoteIdentical.getContent());
    assertEquals(upvoteIdentifierTag, formulaEventUpvoteIdentical.getBadgeDefinitionGenericEvent().getIdentifierTag());

    FormulaEvent formulaEventDownvoteIdentical = new FormulaEvent(formulaCreator, downvoteIdentifierTag, relay, awardDownvoteDefinitionEvent, "-1");
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvoteIdentical), relay);
    cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay).orElseThrow();
    assertEquals(MINUS_ONE_FORMULA, formulaEventDownvoteIdentical.getContent());
    assertEquals(downvoteIdentifierTag, formulaEventDownvoteIdentical.getBadgeDefinitionGenericEvent().getIdentifierTag());
  }
}
