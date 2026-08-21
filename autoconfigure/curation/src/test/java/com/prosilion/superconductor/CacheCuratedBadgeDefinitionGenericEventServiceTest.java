package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.curation.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class CacheCuratedBadgeDefinitionGenericEventServiceTest extends CacheCuratedServiceTestFixture<CuratedBadgeDefinitionGenericEvent> {
  @Mock
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  BadgeDefinitionGenericEvent badgeDefinitionGenericEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getEvent(curatedEventId, relay);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(curatedEventId, relay);
  }

  @Test
  void testGetByDirectEventTagFromLocalCache() {
    EventTag eventTag = curatedEvent.getEventTag();
    doReturn(Optional.of(curatedEvent.asGenericEventRecord()))
       .when(cacheServiceIF)
       .getFirstEventByKindAndEventTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, eventTag);

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(eventTag);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    assertEquals(eventTag, actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndEventTag(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT, eventTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(curatedEventId, relay);
  }

  @Test
  void testGetByDirectEventTagAfterLocalMiss() {
    EventTag eventTag = curatedEvent.getEventTag();
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
  void testGetByAddressTagAfterLocalMiss() {
    AddressTag addressTag = curatedEvent.getAddressTag();
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
    AddressTag addressTag = curatedEvent.getAddressTag();
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
  protected CuratedBadgeDefinitionGenericEvent createEvent() {
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
