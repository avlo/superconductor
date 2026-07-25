package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.aImgIdentity;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheCuratedBadgeDefinitionGenericEventServiceTest extends CacheServiceTestFixture<CuratedBadgeDefinitionGenericEvent> {
  @Mock
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  BadgeDefinitionGenericEvent badgeDefinitionGenericEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetEventFromBadgeDefinitionServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getEvent(eventId, event.getRelay().orElseThrow());

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getEvent(eventId, event.getRelay().orElseThrow());

    assertEquals(event.getEventTag(), actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetByDirectFromLocalCache() {
    EventTag eventTag = event.getEventTag();
    doReturn(Optional.of(event.asGenericEventRecord()))
       .when(cacheServiceIF)
       .getFirstEventByKindAndEventTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, eventTag);

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(eventTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    assertEquals(eventTag, actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndEventTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, eventTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetByDirectFromBadgeDefinitionServiceAfterLocalMiss() {
    EventTag eventTag = event.getEventTag();
    doReturn(Optional.empty()).when(cacheServiceIF).getFirstEventByKindAndEventTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, eventTag);
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getEvent(eventTag.getEventId(), eventTag.requireRelay());

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(eventTag);

    assertEquals(eventTag, actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndEventTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, eventTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getEvent(eventTag.getEventId(), eventTag.requireRelay());
  }

  @Test
  void testGetByAddressTagFromBadgeDefinitionServiceAfterLocalMiss() {
    AddressTag addressTag = event.getAddressTag();
    doReturn(Optional.empty()).when(cacheServiceIF).getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, addressTag);
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getByExpanded(addressTag);
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(addressTag);

    assertEquals(addressTag, actual.map(AbstractSetsEvent::getAddressTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, addressTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testGetByAddressTagReturnsEmptyOptionalWhenNotFoundLocallyOrRemotely() {
    AddressTag addressTag = event.getAddressTag();
    doReturn(Optional.empty()).when(cacheServiceIF).getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, addressTag);
    doReturn(Optional.empty())
       .when(cacheBadgeDefinitionGenericEventService)
       .getByExpanded(addressTag);
    
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual = cacheCuratedBadgeDefinitionGenericEventService.getByDirect(addressTag);

    assertEquals(Optional.empty(), actual);
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, addressTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Override
  CuratedBadgeDefinitionGenericEvent createEvent() {
    this.badgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    return new CuratedBadgeDefinitionGenericEvent(
       aImgIdentity,
       this.badgeDefinitionGenericEvent,
       new ReferenceTag(this.badgeDefinitionGenericEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
  }

  private CacheCuratedBadgeDefinitionGenericEventService createService() {
    return new CacheCuratedBadgeDefinitionGenericEventService(
       aImgIdentity,
       relay.getUrl(),
       cacheServiceIF,
       cacheBadgeDefinitionGenericEventService);
  }
}
