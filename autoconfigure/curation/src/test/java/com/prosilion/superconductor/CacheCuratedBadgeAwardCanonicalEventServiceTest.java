package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardCanonicalEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheCuratedBadgeAwardCanonicalEventService;
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
public class CacheCuratedBadgeAwardCanonicalEventServiceTest extends CacheCuratedServiceTestFixture<CuratedBadgeAwardCanonicalEvent> {
  @Mock
  CacheBadgeAwardCanonicalEventService cacheBadgeAwardCanonicalEventService;
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
    CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService = createService();

    Optional<CuratedBadgeAwardCanonicalEvent> actual =
       cacheCuratedBadgeAwardCanonicalEventService.getEvent(curatedEventId, relay);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
    verify(cacheBadgeAwardCanonicalEventService, Mockito.times(0)).getEvent(curatedEventId, relay);
  }

  @Test
  void testGetEventFromBadgeAwardServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(curatedEventId);
    CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService = createService();

    Optional<CuratedBadgeAwardCanonicalEvent> actual =
       cacheCuratedBadgeAwardCanonicalEventService.getEvent(curatedEventId, relay);

    assertTrue(actual.isEmpty());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
  }

  @Test
  void testGetByPubKeyTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(curatedEvent.getPublicKey());
    doReturn(List.of(curatedEvent.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag);
    CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService = createService();

    List<CuratedBadgeAwardCanonicalEvent> actual = cacheCuratedBadgeAwardCanonicalEventService.getBy(pubKeyTag);

    assertEquals(curatedEventId, actual.stream().map(CuratedBadgeAwardCanonicalEvent::getId).findFirst().orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag);
  }

  @Test
  void testGetByPubKeyTagAndEventTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(curatedEvent.getPublicKey());
    EventTag eventTag = curatedEvent.getEventTag();
    doReturn(List.of(curatedEvent.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndEventTag(Kind.CURATION_SETS_BADGE_AWARD_EVENT, pubKeyTag, eventTag);
    CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService = createService();

    Optional<CuratedBadgeAwardCanonicalEvent> actual =
       cacheCuratedBadgeAwardCanonicalEventService.getBy(pubKeyTag, eventTag);

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
    CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService = createService();

    List<CuratedBadgeAwardCanonicalEvent> actual =
       cacheCuratedBadgeAwardCanonicalEventService.getBy(pubKeyTag, identifierTag);

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
    CacheCuratedBadgeAwardCanonicalEventService cacheCuratedBadgeAwardCanonicalEventService = createService();

    Optional<CuratedBadgeAwardCanonicalEvent> actual =
       cacheCuratedBadgeAwardCanonicalEventService.getBy(pubKeyTag, addressTag);

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
       new CuratedBadgeAwardCanonicalEvent(
          aImgIdentity,
          this.badgeAwardUpvoteEvent,
          this.curatedBadgeDefinitionGenericEvent,
          new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
          relay));
  }

  @Override
  protected CuratedBadgeAwardCanonicalEvent createEvent() {
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

    CuratedBadgeAwardCanonicalEvent curatedBadgeAwardCanonicalEvent = new CuratedBadgeAwardCanonicalEvent(
       aImgIdentity,
       this.badgeAwardUpvoteEvent,
       this.curatedBadgeDefinitionGenericEvent,
       new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
    return curatedBadgeAwardCanonicalEvent;
  }

  private CacheCuratedBadgeAwardCanonicalEventService createService() {
    return new CacheCuratedBadgeAwardCanonicalEventService(
       aImgIdentity,
       relay.getUrl(),
       cacheServiceIF,
       cacheBadgeAwardCanonicalEventService,
       cacheCuratedBadgeDefinitionGenericEventService,
       cacheReferenceEventTagService);
  }
}
