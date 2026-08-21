package com.prosilion.superconductor.event;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class CacheBadgeDefinitionReputationEventServiceIT extends BaseIntegrationTestDirtiesContextFixtures {
  private final CuratedFormulaEvent plusOneCuratedFormulaEvent;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  private final Relay relay;
  private final EventServiceIF eventServiceIF;

  BadgeDefinitionGenericEvent awardDownvoteDefinitionEvent;

  @Autowired
  public CacheBadgeDefinitionReputationEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUri,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheBadgeDefinitionReputationEventService") CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    super(superconductorInstanceIdentity);
    this.eventServiceIF = eventServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
    this.relay = new Relay(relayUri);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    cacheServiceIF.save(awardUpvoteDefinitionEvent);

    this.awardDownvoteDefinitionEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, downvoteIdentifierTag, relay);
    cacheServiceIF.save(this.awardDownvoteDefinitionEvent);

    plusOneCuratedFormulaEvent =
       new CuratedFormulaEvent(
          aImgIdentity,
          new FormulaEvent(
             formulaCreator,
             formulaUpvoteIdentifierTag,
             awardUpvoteDefinitionEvent,
             PLUS_ONE_FORMULA,
             relay),
          new ReferenceTag(relayUri),
          relay);

    eventServiceIF.processIncomingEvent(new EventMessage(plusOneCuratedFormulaEvent), relay);
  }

  @Test
  void testConstructorRejectsNullDependencies() {
    CacheServiceIF cacheServiceIF = mock(CacheServiceIF.class);
    CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF =
       mock(CacheReferenceEventTagServiceIF.class);
    CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF =
       mock(CacheReferenceAddressTagServiceIF.class);
    CacheCuratedFormulaEventServiceIF cacheFormulaEventServiceIF =
       mock(CacheCuratedFormulaEventServiceIF.class);
    CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF =
       mock(CacheKindAddressTagServiceIF.class);

    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheFormulaEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       null,
       cacheReferenceAddressTagServiceIF,
       cacheFormulaEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheFormulaEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       null,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheFormulaEventServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.getEvent(null, relay));
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.getEvent(plusOneCuratedFormulaEvent.getId(), null));
  }

  @Test
  void testGetByExpandedRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.getByExpanded(null));
  }

  @Test
  void testGetByPubKeyTagAndIdentifierTagRejectsNullParameters() {
    PubKeyTag pubKeyTag = new PubKeyTag(repDefnCreator.getPublicKey());

    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.getBy(null, reputationIdentifierTag));
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.getBy(pubKeyTag, null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionReputationEventService.getByDirect(null));
  }

  @Test
  void testGetByDirectAddressTag() {
    BadgeDefinitionReputationEvent expected = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       aImgIdentity.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);
    eventServiceIF.processIncomingEvent(new EventMessage(expected), relay);

    BadgeDefinitionReputationEvent actual = cacheBadgeDefinitionReputationEventService
       .getByDirect(plusOneCuratedFormulaEvent.asAddressableEventAddressTag())
       .orElseThrow();

    assertEquals(expected, actual);
  }

  @Test
  public void testSaveBadgeDefinitionReputationEventUpvote() {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneFormula), relay);
    BadgeDefinitionReputationEvent dbRepDefnEvent = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneFormula.getId(), relay).orElseThrow();
    assertTrue(dbRepDefnEvent.getCuratedFormulaEvents().contains(plusOneCuratedFormulaEvent));
    assertEquals(reputationIdentifierTag, dbRepDefnEvent.getIdentifierTag());
    assertEquals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG, dbRepDefnEvent.getExternalIdentityTag());
    assertEquals(badgeDefinitionReputationEventPlusOneFormula, dbRepDefnEvent);

    assertTrue(dbRepDefnEvent.getCuratedFormulaEvents().stream()
       .map(CuratedFormulaEvent::getFormula)
       .toList().contains(PLUS_ONE_FORMULA));

    assertTrue(dbRepDefnEvent.getCuratedFormulaEvents().stream()
       .map(CuratedFormulaEvent::getAddressTag)
       .map(AddressTag::getIdentifierTag)
       .map(IdentifierTag::getUuid).toList().contains(FORMULA_UNIT_UPVOTE));

    CuratedFormulaEvent minusOneFormulaEvent = new CuratedFormulaEvent(
       aImgIdentity,
       new FormulaEvent(
          formulaCreator,
          formulaDownvoteIdentifierTag,
          awardDownvoteDefinitionEvent,
          MINUS_ONE_FORMULA,
          relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneMinusOne = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       List.of(plusOneCuratedFormulaEvent, minusOneFormulaEvent));

    assertThrows(NostrException.class, () -> cacheBadgeDefinitionReputationEventService.materialize(badgeDefinitionReputationEventPlusOneMinusOne.asGenericEventRecord()));

    eventServiceIF.processIncomingEvent(new EventMessage(minusOneFormulaEvent), relay);
    eventServiceIF.processIncomingEvent(new EventMessage(badgeDefinitionReputationEventPlusOneMinusOne), relay);

    BadgeDefinitionReputationEvent dbRepDefnEventPlusMinus = cacheBadgeDefinitionReputationEventService.getEvent(badgeDefinitionReputationEventPlusOneMinusOne.getId(), relay).orElseThrow();
    assertTrue(dbRepDefnEventPlusMinus.getCuratedFormulaEvents().contains(plusOneCuratedFormulaEvent));
    assertTrue(dbRepDefnEventPlusMinus.getCuratedFormulaEvents().contains(minusOneFormulaEvent));
    assertEquals(reputationIdentifierTag, dbRepDefnEventPlusMinus.getIdentifierTag());
    assertEquals(BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG, dbRepDefnEventPlusMinus.getExternalIdentityTag());
    assertEquals(badgeDefinitionReputationEventPlusOneMinusOne, dbRepDefnEventPlusMinus);
    assertTrue(dbRepDefnEventPlusMinus.getCuratedFormulaEvents().stream()
       .map(CuratedFormulaEvent::getFormula)
       .toList().contains(MINUS_ONE_FORMULA));
    assertTrue(dbRepDefnEventPlusMinus.getCuratedFormulaEvents().stream()
       .map(CuratedFormulaEvent::getAddressTag)
       .map(AddressTag::getIdentifierTag)
       .toList().contains(formulaDownvoteIdentifierTag));

    BadgeDefinitionReputationEvent reconstructed = cacheBadgeDefinitionReputationEventService.materialize(badgeDefinitionReputationEventPlusOneMinusOne.asGenericEventRecord()).orElseThrow();
    assertEquals(dbRepDefnEventPlusMinus, reconstructed);
  }


  @Test
  public void testGetByPubKeyTagIdentifierTag() {
    BadgeDefinitionReputationEvent expected = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);

    eventServiceIF.processIncomingEvent(new EventMessage(expected), relay);

    Optional<BadgeDefinitionReputationEvent> actual =
       cacheBadgeDefinitionReputationEventService.getBy(
          new PubKeyTag(repDefnCreator.getPublicKey()), reputationIdentifierTag);

    assertTrue(actual.isPresent());
    assertEquals(expected.getId(), actual.map(BadgeDefinitionGenericEvent::getId).orElseThrow());
    assertEquals(expected, actual.orElseThrow());
  }
}
