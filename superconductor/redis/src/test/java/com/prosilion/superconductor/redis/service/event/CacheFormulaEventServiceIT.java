package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.CacheFormulaEventService;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.service.RedisCacheService;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.Optional;
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

  public static final String UNIT_REPUTATION = "UNIT_REPUTATION";
  public static final String UNIT_UPVOTE = "UNIT_UPVOTE";
  public static final String UNIT_DOWNVOTE = "UNIT_DOWNVOTE";

  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);
//  public final IdentifierTag downvoteIdentifierTag = new IdentifierTag(UNIT_DOWNVOTE);

  public final Identity identity = Identity.generateRandomIdentity();

  public final String PLUS_ONE_FORMULA = "+1";
//  public final String MINUS_ONE_FORMULA = "-1";

  final BadgeDefinitionAwardEvent awardUpvoteEvent = new BadgeDefinitionAwardEvent(identity, upvoteIdentifierTag, relay, PLUS_ONE_FORMULA);
//  final BadgeDefinitionAwardEvent awardDownvoteEvent = new BadgeDefinitionAwardEvent(identity, downvoteIdentifierTag, relay, MINUS_ONE_FORMULA);

  public static final String PLATFORM = CacheFormulaEventServiceIT.class.getPackageName();
  public static final String IDENTITY = CacheFormulaEventServiceIT.class.getSimpleName();
  public static final String PROOF = String.valueOf(CacheFormulaEventServiceIT.class.hashCode());

  final FormulaEvent formulaEventUpvote;
  //  final FormulaEvent formulaEventDownvote;
  final ExternalIdentityTag externalIdentityTag;

  private final EventPluginIF eventPlugin;
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheFormulaEventServiceIT(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("cacheFormulaEventService") CacheFormulaEventService cacheFormulaEventService,
      @NonNull @Qualifier("cacheService") RedisCacheService cacheService) throws ParseException {
    this.eventPlugin = eventPlugin;
    this.cacheFormulaEventService = cacheFormulaEventService;

    this.formulaEventUpvote = new FormulaEvent(identity, awardUpvoteEvent, PLUS_ONE_FORMULA);
//    this.formulaEventDownvote = new FormulaEvent(identity, awardDownvoteEvent, MINUS_ONE_FORMULA);
    this.externalIdentityTag = new ExternalIdentityTag(PLATFORM, IDENTITY, PROOF);

    eventPlugin.processIncomingEvent(awardUpvoteEvent);
    Optional<GenericEventRecord> eventByEventId = cacheService.getEventByEventId(awardUpvoteEvent.getId());
    assertEquals(awardUpvoteEvent.getId(), eventByEventId.map(GenericEventRecord::getId).orElseThrow());
  }

  @Test
  public void testSave() {
    eventPlugin.processIncomingEvent(formulaEventUpvote);
    FormulaEvent formulaEvent = cacheFormulaEventService.getFormulaEvent(formulaEventUpvote.getId()).orElseThrow();
    assertEquals(formulaEventUpvote, formulaEvent);
  }
}
