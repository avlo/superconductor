package com.prosilion.superconductor.tag;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheReferenceEventTagServiceUsingBadgeDefinitionGenericEventTest extends CacheServiceTestFixture<BadgeDefinitionGenericEvent> {
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
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(anyString(), any(Filters.class));

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetEventByNonExistentEventIdReturnsReturnsRemoteObject() {
    CacheReferenceEventTagService cacheReferenceEventTagServiceSpy =
       spy(new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService));
    
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagServiceSpy)
       .getByExpanded(new EventTag(event.getId(), relay.getUrl()));
    
    String actualEventIdViaEventTagService = cacheReferenceEventTagServiceSpy.getEvent(eventId, relay).orElseThrow().getId();

    assertEquals(eventId, actualEventIdViaEventTagService);
    verify(cacheReferenceEventTagServiceSpy).getByExpanded(
       new EventTag(eventId, relay.getUrl()));
    
    Optional<GenericEventRecord> actualLocalShouldBeEmptyOptional = cacheReferenceEventTagServiceSpy.getEvent(Util.generateRandomHex64String(), relay);
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(anyString());
    verify(remoteAbstractTagService, Mockito.times(1)).sendRemoteReq(anyString(), any(Filters.class));

    assertEquals(Optional.empty(), actualLocalShouldBeEmptyOptional);
  }

  @Override
  protected BadgeDefinitionGenericEvent createEvent() {
    return new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
  }
}
