package com.prosilion.superconductor.event;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheBadgeDefinitionGenericEventServiceTest extends CacheServiceTestFixture<BadgeDefinitionGenericEvent> {
  @Mock
  CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  @Mock
  CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionGenericEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionGenericEventService(
       cacheServiceIF,
       null,
       cacheReferenceAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService = createService();

    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionGenericEventService.getEvent(null, relay));
    assertThrows(NullPointerException.class, () ->
       cacheBadgeDefinitionGenericEventService.getEvent(eventId, null));
  }

  @Test
  void testGetByExpandedRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () ->
       createService().getByExpanded((AddressTag) null));
  }

  @Test
  void testGetEventByEventIdFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService = createService();

    Optional<BadgeDefinitionGenericEvent> actual =
       cacheBadgeDefinitionGenericEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
  }

  @Test
  void testGetEventByEventIdFromEventTagCacheLookup() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndEventTag(event.getKind(), new EventTag(eventId));
    CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService = createService();

    Optional<BadgeDefinitionGenericEvent> actual =
       cacheBadgeDefinitionGenericEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndEventTag(
       event.getKind(), new EventTag(eventId));
  }

  @Test
  void testGetEventByEventIdFromRemoteReferenceService() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndEventTag(event.getKind(), new EventTag(eventId));
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagServiceIF)
       .getEvent(eventId, relay);
    CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService = createService();

    Optional<BadgeDefinitionGenericEvent> actual =
       cacheBadgeDefinitionGenericEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagServiceIF, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetByExpandedAddressTag() {
    AddressTag addressTag = event.asAddressableEventAddressTag();
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceAddressTagServiceIF)
       .getByExpanded(addressTag);
    CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService = createService();

    Optional<BadgeDefinitionGenericEvent> actual =
       cacheBadgeDefinitionGenericEventService.getByExpanded(addressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testMaterialize() {
    CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService = createService();

    Optional<BadgeDefinitionGenericEvent> actual =
       cacheBadgeDefinitionGenericEventService.materialize(event.getGenericEventRecord());

    assertEquals(eventId, actual.orElseThrow().getId());
  }

  @Override
  protected BadgeDefinitionGenericEvent createEvent() {
    return new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
  }

  private CacheBadgeDefinitionGenericEventService createService() {
    return new CacheBadgeDefinitionGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF);
  }
}
