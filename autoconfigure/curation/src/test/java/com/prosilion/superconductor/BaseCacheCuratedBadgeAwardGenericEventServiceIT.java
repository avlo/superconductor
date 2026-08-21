package com.prosilion.superconductor;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.SetsPairedEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseCacheCuratedBadgeAwardGenericEventServiceIT extends BaseIntegrationTestDirtiesContextFixtures {
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;
  private final CacheServiceIF cacheServiceIF;
  private final Relay relay;

  CuratedBadgeAwardGenericEvent curationSetsUpvoteEvent;

  public BaseCacheCuratedBadgeAwardGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF) {
    super(superconductorInstanceIdentity);
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
    this.cacheServiceIF = cacheServiceIF;
    this.relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);

    CuratedFormulaEvent plusOneCuratedFormulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relayUrl),
       relay);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
       superconductorInstanceIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       plusOneCuratedFormulaEvent);

    AddressTag badgeDefnEventAsAddressTag = badgeAwardUpvoteEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag();

    SetsPairedEvent setsPairedEvents = new SetsPairedEvent(
       badgeDefnEventAsAddressTag,
       new EventTag(badgeAwardUpvoteEvent.getId(), badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElse(null)));

    this.curationSetsUpvoteEvent = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardUpvoteEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    cacheServiceIF.save(curationSetsUpvoteEvent);
  }

  @Test
  public void testGetEventByPubKeyTag() {
    List<CuratedBadgeAwardGenericEvent> byPubKeyTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getBy(
          new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()));
    assertEquals(1, byPubKeyTag.size());
    assertEquals(curationSetsUpvoteEvent, byPubKeyTag.getFirst());
  }

  @Test
  public void testGetEventByPubKeyTagEventTag() {
    Optional<CuratedBadgeAwardGenericEvent> byPubKeyTagEventTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getBy(
          new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()),
          curationSetsUpvoteEvent.getEventTag());
    assertTrue(byPubKeyTagEventTag.isPresent());
  }

  @Test
  public void testGetEventByPubKeyTagIdentifierTag() {
    List<CuratedBadgeAwardGenericEvent> byPubKeyTagIdentifierTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getBy(new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()), curationSetsUpvoteEvent.getIdentifierTag());
    assertEquals(1, byPubKeyTagIdentifierTag.size());
    assertEquals(curationSetsUpvoteEvent, byPubKeyTagIdentifierTag.getFirst());
  }

  @Test
  public void testGetEventByPubKeyTagAddressTag() {
    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventServiceIF.getBy(
          new PubKeyTag(curationSetsUpvoteEvent.getAwardRecipientPublicKey()),
          curationSetsUpvoteEvent.getAddressTag());

    assertEquals(curationSetsUpvoteEvent, actual.orElseThrow());
  }

  @Test
  public void testGetEventByEventTag() {
    Optional<CuratedBadgeAwardGenericEvent> byPubKeyTagIdentifierTag = cacheCuratedBadgeAwardGenericEventServiceIF
       .getByDirect(curationSetsUpvoteEvent.getEventTag());
    assertTrue(byPubKeyTagIdentifierTag.isPresent());
    assertEquals(curationSetsUpvoteEvent, byPubKeyTagIdentifierTag.get());
  }

  @Test
  public void testGetByDirectEventTagFromBackingServiceAfterLocalMiss() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       createAndSaveBackingBadgeAward();
    EventTag eventTag = new EventTag(
       badgeAwardGenericEvent.getId(),
       badgeAwardGenericEvent.getRelay().map(Relay::getUrl).orElseThrow());

    CuratedBadgeAwardGenericEvent actual =
       cacheCuratedBadgeAwardGenericEventServiceIF.getByDirect(eventTag).orElseThrow();

    assertEquals(eventTag, actual.getEventTag());
    assertEquals(actual, cacheCuratedBadgeAwardGenericEventServiceIF.getEvent(actual.getId(), relay).orElseThrow());
  }

  @Test
  public void testGetByDirectAddressTagFromBackingServiceAfterLocalMiss() {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       createAndSaveBackingBadgeAward();
    AddressTag addressTag =
       badgeAwardGenericEvent.getBadgeDefinitionEvent().asAddressableEventAddressTag();

    CuratedBadgeAwardGenericEvent actual =
       cacheCuratedBadgeAwardGenericEventServiceIF.getByDirect(addressTag).orElseThrow();

    assertEquals(badgeAwardGenericEvent.getId(), actual.getEventTag().getEventId());
    assertEquals(actual, cacheCuratedBadgeAwardGenericEventServiceIF
       .getEvent(actual.getId(), relay)
       .orElseThrow());
  }

  @Test
  public void testGetEventByEventIdRelay() {
    String eventId = curationSetsUpvoteEvent.getEventId();
    Optional<CuratedBadgeAwardGenericEvent> byEventIdRelay = cacheCuratedBadgeAwardGenericEventServiceIF
       .getEvent(eventId, relay);
    assertTrue(byEventIdRelay.isPresent());
    assertEquals(curationSetsUpvoteEvent, byEventIdRelay.get());

    Optional<CuratedBadgeAwardGenericEvent> byEventIdNonExistentRelay = cacheCuratedBadgeAwardGenericEventServiceIF
       .getEvent(eventId, new Relay("ws://localhost-non-existent:5555"));
    assertTrue(byEventIdNonExistentRelay.isPresent());
    assertEquals(curationSetsUpvoteEvent, byEventIdNonExistentRelay.get());
  }

  @Test
  public void testNonExistentEventIdReturnsEmptyOptional() {
    String nonExistentEventId = Util.generateRandomHex64String();
    assertEquals(Optional.empty(), cacheCuratedBadgeAwardGenericEventServiceIF.getEvent(nonExistentEventId, relay));
  }

  @Test
  public void testNonExistentEventTagEventIdThrowsException() {
    String nonExistentEventId = Util.generateRandomHex64String();
    EventTag nonExistentEventTagEventId = new EventTag(nonExistentEventId);
    assertThrows(NostrException.class, () -> cacheCuratedBadgeAwardGenericEventServiceIF.getByDirect(nonExistentEventTagEventId));
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAndSaveBackingBadgeAward() {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent =
       new BadgeDefinitionGenericEvent(
          Identity.generateRandomIdentity(),
          upvoteIdentifierTag,
          relay);
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          Identity.generateRandomIdentity(),
          recipient.getPublicKey(),
          badgeDefinitionGenericEvent,
          relay);
    cacheServiceIF.save(badgeDefinitionGenericEvent);
    cacheServiceIF.save(badgeAwardGenericEvent);
    return badgeAwardGenericEvent;
  }
}
