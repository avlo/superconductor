package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

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
    this.formulaEventUpvote = new FormulaEvent(formulaCreator, upvoteIdentifierTag, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA, relay);

    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(repDefnCreator, downvoteIdentifierTag, MINUS_ONE_FORMULA, relay);
    this.formulaEventDownvote = new FormulaEvent(formulaCreator, downvoteIdentifierTag, awardDownvoteDefinitionEvent, MINUS_ONE_FORMULA, relay);

    cacheServiceIF.save(awardUpvoteDefinitionEvent);
    cacheServiceIF.save(awardDownvoteDefinitionEvent);
  }

  @Test
  void testConstructorRejectsNullDependencies() {
    CacheReferenceEventTagService cacheReferenceEventTagService =
       mock(CacheReferenceEventTagService.class);
    CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF =
       mock(CacheReferenceAddressTagServiceIF.class);
    CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF =
       mock(CacheKindAddressTagServiceIF.class);

    assertThrows(NullPointerException.class, () -> new CacheFormulaEventService(
       null,
       cacheReferenceAddressTagServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFormulaEventService(
       cacheReferenceEventTagService,
       null,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFormulaEventService(
       cacheReferenceEventTagService,
       cacheReferenceAddressTagServiceIF,
       null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getEvent(null, relay));
    assertThrows(NullPointerException.class, () ->
       cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.materialize((EventIF) null));
  }

  @Test
  void testGetByPublicKeyIdentifierTagAndRelayRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getBy(
       null, upvoteIdentifierTag, relay));
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getBy(
       formulaCreator.getPublicKey(), (IdentifierTag) null, relay));
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getBy(
       formulaCreator.getPublicKey(), upvoteIdentifierTag, null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getByDirect(null));
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

    FormulaEvent formulaEventUpvoteIdentical = new FormulaEvent(formulaCreator, upvoteIdentifierTag, awardUpvoteDefinitionEvent, "+1", relay);
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventUpvoteIdentical), relay);
    dbPlusOneFormulaEvent = cacheFormulaEventService.getEvent(formulaEventUpvote.getId(), relay).orElseThrow();
    assertEquals(PLUS_ONE_FORMULA, formulaEventUpvoteIdentical.getContent());
    assertEquals(upvoteIdentifierTag, formulaEventUpvoteIdentical.getBadgeDefinitionGenericEvent().getIdentifierTag());

    FormulaEvent formulaEventDownvoteIdentical = new FormulaEvent(formulaCreator, downvoteIdentifierTag, awardDownvoteDefinitionEvent, "-1", relay);
    eventServiceIF.processIncomingEvent(new EventMessage(formulaEventDownvoteIdentical), relay);
    cacheFormulaEventService.getEvent(formulaEventDownvote.getId(), relay).orElseThrow();
    assertEquals(MINUS_ONE_FORMULA, formulaEventDownvoteIdentical.getContent());
    assertEquals(downvoteIdentifierTag, formulaEventDownvoteIdentical.getBadgeDefinitionGenericEvent().getIdentifierTag());
  }
}
