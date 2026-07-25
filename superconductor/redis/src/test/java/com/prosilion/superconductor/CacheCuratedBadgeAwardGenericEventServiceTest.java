package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedBadgeAwardGenericEventService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.aImgIdentity;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.recipient;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.submitter;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CacheCuratedBadgeAwardGenericEventServiceTest extends CacheServiceTestFixture<CuratedBadgeAwardGenericEvent> {
  @Mock
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService;

  BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetEventFromBadgeAwardServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(Optional.of(badgeAwardUpvoteEvent))
       .when(cacheBadgeAwardGenericEventService)
       .getEvent(eventId, relay);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getEvent(eventId, relay);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(1)).getEvent(eventId, relay);
    verify(cacheServiceIF, Mockito.times(1)).save(actual.orElseThrow());
  }

  @Test
  void testGetByDirectFromLocalCache() {
    EventTag eventTag = event.getEventTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, eventTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getByDirect(eventTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, eventTag);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(0)).getEvent(eventTag.getEventId(), eventTag.requireRelay());
  }

  @Test
  void testGetByDirectFromBadgeAwardServiceAfterLocalMiss() {
    EventTag eventTag = event.getEventTag();
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, eventTag);
    doReturn(Optional.of(badgeAwardUpvoteEvent))
       .when(cacheBadgeAwardGenericEventService)
       .getEvent(eventTag.getEventId(), eventTag.requireRelay());
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getByDirect(eventTag);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, eventTag);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(1)).getEvent(
       eventTag.getEventId(), eventTag.requireRelay());
    verify(cacheServiceIF, Mockito.times(1)).save(actual.orElseThrow());
  }

  @Test
  void testGetByPubKeyTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    List<CuratedBadgeAwardGenericEvent> actual = cacheCuratedBadgeAwardGenericEventService.getBy(pubKeyTag);

    assertEquals(eventId, actual.stream().map(CuratedBadgeAwardGenericEvent::getId).findFirst().orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag);
  }

  @Test
  void testGetByPubKeyTagAndEventTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    EventTag eventTag = event.getEventTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, eventTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getBy(pubKeyTag, eventTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndEventTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, eventTag);
  }

  @Test
  void testGetByPubKeyTagAndIdentifierTagReturnsEmptyList() {
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    IdentifierTag identifierTag = new IdentifierTag(String.valueOf(event.getAddressTag().hashCode()));
    doReturn(List.of())
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, identifierTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    List<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getBy(pubKeyTag, identifierTag);

    assertEquals(List.of(), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, identifierTag);
  }

  @Test
  void testGetByPubKeyTagAndAddressTagReturnsEmptyOptional() {
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    AddressTag addressTag = event.getAddressTag();
    doReturn(List.of())
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndAddressTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, addressTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getBy(pubKeyTag, addressTag);

    assertEquals(Optional.empty(), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndAddressTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, addressTag);
  }

  @Test
  void zTestGetEventFromLocalCacheNullRelay() {
    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       relay);

    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(  // <------------------------- no relay
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent);

    assertThrows(NoSuchElementException.class, () ->
       new CuratedBadgeAwardGenericEvent(
          aImgIdentity,
          this.badgeAwardUpvoteEvent,
          new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
          new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
          relay));
  }

  @Override
  CuratedBadgeAwardGenericEvent createEvent() {
    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       relay);
    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);                                                  // <------------------------- has relay

    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       aImgIdentity,
       this.badgeAwardUpvoteEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    return curatedBadgeAwardGenericEvent;
  }

  private CacheCuratedBadgeAwardGenericEventService createService() {
    return new CacheCuratedBadgeAwardGenericEventService(
       aImgIdentity,
       relay.getUrl(),
       cacheServiceIF,
       cacheBadgeAwardGenericEventService);
  }
}
