package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheReferenceEventTagServiceTest extends CacheServiceTestFixture<BadgeDefinitionGenericEvent> {
  @Test
  void testGetEventByEventId() {
    mockLocalGetEventByEventId();

    CacheReferenceEventTagService cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);

//  invoke...
    String actualEventIdViaEventTagService = cacheReferenceEventTagService.getEvent(
       eventId, relay).orElseThrow().getId();
    assertEquals(eventId, actualEventIdViaEventTagService);

//  ... verify invocation
    verify(cacheServiceIF).getEventByEventId(eventId);
  }

  @Test
  void testGetEventByEventIdCalledOnce() {
    mockLocalGetEventByEventId();

    CacheReferenceEventTagService cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);

    cacheReferenceEventTagService.getEvent(eventId, relay);
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(anyString());
  }

  @Test
  void testGetEventByNonExistentEventIdReturnsEmptyOptional() {
    mockLocalGetEventByAnyReturnsEmptyOptional();
    mockLocalGetEventByEventId();

    CacheReferenceEventTagService cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);

    cacheReferenceEventTagService.getEvent(eventId, relay);
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(anyString());

    Optional<GenericEventRecord> actual = cacheReferenceEventTagService.getEvent(Util.generateRandomHex64String(), relay);
    verify(cacheServiceIF, Mockito.times(2)).getEventByEventId(anyString());
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(
       anyString(), any(Filters.class));

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetEventByNonExistentEventIdReturnsReturnsRemoteObject() {
    mockLocalGetEventByEventIdReturnsEmptyOptional(event);
    mockRemoveGetEventByEventId(event);

    CacheReferenceEventTagService cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);

    String actualEventIdViaEventTagService = cacheReferenceEventTagService.getEvent(eventId, relay).orElseThrow().getId();

    assertEquals(eventId, actualEventIdViaEventTagService);
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(anyString());
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(anyString(), any(Filters.class));

    Optional<GenericEventRecord> actualLocalShouldBeEmptyOptional = cacheReferenceEventTagService.getEvent(Util.generateRandomHex64String(), relay);
    verify(cacheServiceIF, Mockito.times(2)).getEventByEventId(anyString());

//    assertEquals(Optional.empty(), actualLocalShouldBeEmptyOptional);
  }

  @Override
  BadgeDefinitionGenericEvent createEvent() {
    return new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
  }
}
