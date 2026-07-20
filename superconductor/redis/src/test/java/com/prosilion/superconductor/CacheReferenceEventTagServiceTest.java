package com.prosilion.superconductor;

import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class CacheReferenceEventTagServiceTest extends CacheServiceTestFixture {
  @Test
  @Order(1)
  void testGetEventByEventId() {
    CacheReferenceEventTagService cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);
//  invoke...
    String actualEventIdViaEventTagService = cacheReferenceEventTagService.getEvent(eventId, relay).orElseThrow().getId();
    assertEquals(eventId, actualEventIdViaEventTagService);

//  ... verify invocation
    verify(cacheServiceIF).getEventByEventId(eventId);
  }

  @Test
  @Order(2)
  void testGetEventByEventIdCalledOnce() {
    CacheReferenceEventTagService cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);

    cacheReferenceEventTagService.getEvent(eventId, relay);
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(anyString());
  }
}
