package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.service.RedisCacheService;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheFormulaEventServiceIT {
  public static final Relay relay = new Relay("ws://localhost:5555");

  public static final String UNIT_UPVOTE = "UNIT_UPVOTE";
  public static final String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);
  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public final String PLUS_ONE_FORMULA = "+1";
  public final String MINUS_ONE_FORMULA = "-1";

  final BadgeDefinitionAwardEvent awardUpvoteDefinitionEvent; // = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
  final BadgeDefinitionAwardEvent awardDownvoteDefinitionEvent; // = new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA);

  final FormulaEvent formulaEventUpvote;
  final FormulaEvent formulaEventDownvote;

  private final EventPluginIF eventPlugin;
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheFormulaEventServiceIT(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheFormulaEventService") CacheFormulaEventService cacheFormulaEventService,
      @NonNull @Qualifier("cacheService") RedisCacheService cacheService,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) throws ParseException {
    this.eventPlugin = eventPlugin;
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.awardUpvoteDefinitionEvent = badgeDefinitionUpvoteEvent;
    this.awardDownvoteDefinitionEvent = badgeDefinitionDownvoteEvent;

    this.formulaEventUpvote = new FormulaEvent(identity, upvoteIdentifierTag, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
    this.formulaEventDownvote = new FormulaEvent(identity, downvoteIdentifierTag, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA);

    eventPlugin.processIncomingEvent(awardUpvoteDefinitionEvent);
    GenericEventRecord dbAwardUpvoteEvent = cacheService.getEventByEventId(awardUpvoteDefinitionEvent.getId()).orElseThrow();
    assertEquals(awardUpvoteDefinitionEvent.getId(), dbAwardUpvoteEvent.getId());
    assertEquals(UNIT_UPVOTE, Filterable.getTypeSpecificTags(IdentifierTag.class, dbAwardUpvoteEvent).getFirst().getUuid());

    eventPlugin.processIncomingEvent(awardDownvoteDefinitionEvent);
    GenericEventRecord dbAwardDownvoteEvent = cacheService.getEventByEventId(awardDownvoteDefinitionEvent.getId()).orElseThrow();
    assertEquals(awardDownvoteDefinitionEvent.getId(), dbAwardDownvoteEvent.getId());
    assertEquals(UNIT_DOWNVOTE, Filterable.getTypeSpecificTags(IdentifierTag.class, dbAwardDownvoteEvent).getFirst().getUuid());
  }

  @Test
  public void testSaveFormulaUpvote() {
    cacheFormulaEventService.save(formulaEventUpvote);
    eventPlugin.processIncomingEvent(formulaEventUpvote);
    FormulaEvent dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId()).orElseThrow();
    assertEquals(formulaEventUpvote, dbPlusOneFormulaEvent);
    assertEquals(PLUS_ONE_FORMULA, dbPlusOneFormulaEvent.getContent());
    assertEquals(UNIT_UPVOTE, dbPlusOneFormulaEvent.getBadgeDefinitionAwardEvent().getIdentifierTag().getUuid());
  }

  @Test
  public void testSaveFormulaDownvote() {
    cacheFormulaEventService.save(formulaEventDownvote);
    eventPlugin.processIncomingEvent(formulaEventDownvote);
    FormulaEvent dbMinusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventDownvote.getId()).orElseThrow();
    assertEquals(formulaEventDownvote, dbMinusOneFormulaEvent);
    assertEquals(MINUS_ONE_FORMULA, dbMinusOneFormulaEvent.getContent());
    assertEquals(UNIT_DOWNVOTE, dbMinusOneFormulaEvent.getBadgeDefinitionAwardEvent().getIdentifierTag().getUuid());
  }
}
