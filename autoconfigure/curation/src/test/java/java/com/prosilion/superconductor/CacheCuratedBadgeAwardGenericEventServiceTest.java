package java.com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.award.CacheCuratedBadgeAwardGenericEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
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

  BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent;
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardUpvoteEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getEvent(curatedEventId, BaseIntegrationTestFixtures.relay);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(0)).getEvent(curatedEventId, BaseIntegrationTestFixtures.relay);
  }

  @Test
  void testGetEventFromBadgeAwardServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(curatedEventId);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getEvent(curatedEventId, BaseIntegrationTestFixtures.relay);

    assertTrue(actual.isEmpty());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
  }

  @Test
  void testGetByDirectEventTagFromBadgeAwardServiceAfterLocalMiss() {
    EventTag eventTag = curatedEvent.getEventTag();
    doReturn(Optional.empty()).when(cacheServiceIF).getFirstEventByKindAndEventTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, eventTag);
    doReturn(Optional.of(badgeAwardUpvoteEvent))
       .when(cacheBadgeAwardGenericEventService)
       .getEvent(eventTag.getEventId(), eventTag.requireRelay());
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getByDirect(eventTag);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndEventTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, eventTag);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(1)).getEvent(
       eventTag.getEventId(), eventTag.requireRelay());
    verify(cacheServiceIF, Mockito.times(1)).save(actual.orElseThrow());
  }

  @Test
  void testGetByDirectAddressTagFromBadgeAwardServiceAfterLocalMiss() {
    AddressTag addressTag = curatedEvent.getAddressTag();
    doReturn(Optional.empty()).when(cacheServiceIF).getFirstEventByKindAndAddressTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, addressTag);
    doReturn(Optional.of(badgeAwardUpvoteEvent))
       .when(cacheBadgeAwardGenericEventService)
       .getByDirect(addressTag);
    CacheCuratedBadgeAwardGenericEventService cacheCuratedBadgeAwardGenericEventService = createService();

    Optional<CuratedBadgeAwardGenericEvent> actual =
       cacheCuratedBadgeAwardGenericEventService.getByDirect(addressTag);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndAddressTag(
       Kind.CURATION_SETS_BADGE_AWARD_EVENT, addressTag);
    verify(cacheBadgeAwardGenericEventService, Mockito.times(1)).getByDirect(addressTag);
    verify(cacheServiceIF, Mockito.times(1)).save(actual.orElseThrow());
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
       BaseIntegrationTestFixtures.upvoteDefnCreator,
       BaseIntegrationTestFixtures.upvoteIdentifierTag,
       BaseIntegrationTestFixtures.relay);

    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(  // <------------------------- no relay
       BaseIntegrationTestFixtures.submitter,
       BaseIntegrationTestFixtures.recipient.getPublicKey(),
       awardUpvoteDefinitionEvent);

    assertThrows(NoSuchElementException.class, () ->
       new CuratedBadgeAwardGenericEvent(
          BaseIntegrationTestFixtures.aImgIdentity,
          this.badgeAwardUpvoteEvent,
          new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
          new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
          BaseIntegrationTestFixtures.relay));
  }

  @Override
  protected CuratedBadgeAwardGenericEvent createEvent() {
    this.awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       BaseIntegrationTestFixtures.upvoteDefnCreator,
       BaseIntegrationTestFixtures.upvoteIdentifierTag,
       BaseIntegrationTestFixtures.relay);
    this.badgeAwardUpvoteEvent = new BadgeAwardGenericEvent<>(
       BaseIntegrationTestFixtures.submitter,
       BaseIntegrationTestFixtures.recipient.getPublicKey(),
       awardUpvoteDefinitionEvent,
       BaseIntegrationTestFixtures.relay);                                                  // <------------------------- has relay

    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       BaseIntegrationTestFixtures.aImgIdentity,
       this.badgeAwardUpvoteEvent,
       new ReferenceTag(awardUpvoteDefinitionEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       new ReferenceTag(badgeAwardUpvoteEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       BaseIntegrationTestFixtures.relay);
    return curatedBadgeAwardGenericEvent;
  }

  private CacheCuratedBadgeAwardGenericEventService createService() {
    return new CacheCuratedBadgeAwardGenericEventService(
       BaseIntegrationTestFixtures.aImgIdentity,
       BaseIntegrationTestFixtures.relay.getUrl(),
       cacheServiceIF,
       cacheBadgeAwardGenericEventService);
  }
}
