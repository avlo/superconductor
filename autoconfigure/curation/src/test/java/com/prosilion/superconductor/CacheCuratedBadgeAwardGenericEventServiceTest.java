package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheCuratedBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
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
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class CacheCuratedBadgeAwardGenericEventServiceTest extends CacheCuratedServiceTestFixture<CuratedBadgeAwardGenericEvent> {
  @Mock
  CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService;
  @Mock
  CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService;
  @Mock
  CacheReferenceEventTagService cacheReferenceEventTagService;

  BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  BadgeAwardCanonicalEvent badgeAwardUpvoteEvent;
  CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getEvent(curatedEventId, relay);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(0)).getEvent(curatedEventId, relay);
  }

  @Test
  void testGetEventFromBadgeAwardServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(curatedEventId);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getEvent(curatedEventId, relay);

    assertTrue(actual.isEmpty());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
  }

  @Test
  void testGetByPubKeyTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(curatedEvent.getPublicKey());
    doReturn(List.of(curatedEvent.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    List<CuratedBadgeAwardGenericEvent> actual = cacheCuratedBadgeAwardGenericEventService.getBy(pubKeyTag);

    assertEquals(curatedEventId, actual.stream().map(CuratedBadgeAwardGenericEvent::getId).findFirst().orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag);
  }

  @Test
  void testGetByPubKeyTagAndEventTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(curatedEvent.getPublicKey());
    EventTag eventTag = curatedEvent.getEventTag();
    doReturn(List.of(curatedEvent.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, eventTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getBy(pubKeyTag, eventTag);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndEventTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, eventTag);
  }

  @Test
  void testGetByPubKeyTagAndIdentifierTagReturnsEmptyList() {
    PubKeyTag pubKeyTag = new PubKeyTag(curatedEvent.getPublicKey());
    IdentifierTag identifierTag = AbstractSetsEvent.hashedAddressTag(curatedEvent.getAddressTag());
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
    PubKeyTag pubKeyTag = new PubKeyTag(curatedEvent.getPublicKey());
    AddressTag addressTag = curatedEvent.getAddressTag();
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

    this.badgeAwardUpvoteEvent = new BadgeAwardCanonicalEvent(  // <------------------------- no relay
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent);

    this.curatedBadgeDefinitionGenericEvent = new CuratedBadgeDefinitionGenericEvent(
       aImgIdentity,
       awardUpvoteDefinitionEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);

    assertThrows(NoSuchElementException.class, () ->
       new CuratedBadgeAwardGenericEvent(
          aImgIdentity,
          this.badgeAwardUpvoteEvent,
          this.curatedBadgeDefinitionGenericEvent,
          new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
          relay));
  }

  @Override
  protected CuratedBadgeAwardGenericEvent createEvent() {
    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       relay);
    this.badgeAwardUpvoteEvent = new BadgeAwardCanonicalEvent(
       submitter,
       recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       relay);                                                  // <------------------------- has relay
    this.curatedBadgeDefinitionGenericEvent = new CuratedBadgeDefinitionGenericEvent(
       aImgIdentity,
       awardUpvoteDefinitionEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);

    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       aImgIdentity,
       this.badgeAwardUpvoteEvent,
       this.curatedBadgeDefinitionGenericEvent,
       new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    return curatedBadgeAwardGenericEvent;
  }

  private CacheCuratedBadgeAwardGenericEventService createService() {
    return new CacheCuratedBadgeAwardGenericEventService(
       aImgIdentity,
       relay.getUrl(),
       cacheServiceIF,
       cacheBadgeAwardGenericEventService,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheReferenceEventTagService);
  }
}
