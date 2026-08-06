package com.prosilion.superconductor;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.RemoteAbstractTagService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

public abstract class CacheServiceTestFixture<T extends BaseEvent> {
  @Mock
  protected CacheServiceIF cacheServiceIF;
  @Mock
  protected RemoteAbstractTagService remoteAbstractTagService;

  protected T event;
  protected String eventId;

  @BeforeEach
  public void setUp() {
    this.event = createEvent();
    this.eventId = event.getId();
  }

  protected abstract T createEvent();

  protected void mockLocalGetEventByEventId() {
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventByEventId(eq(eventId));
  }

  protected void mockLocalGetEventByAnyReturnsEmptyOptional() {
    doReturn(Optional.empty())
       .when(cacheServiceIF)
       .getEventByEventId(anyString());
  }

  protected <U extends BaseEvent> void mockLocalGetEventByEventIdReturnsEmptyOptional(U remoteEvent) {
    doReturn(Optional.empty())
       .when(cacheServiceIF)
       .getEventByEventId(remoteEvent.getId());
  }

  protected <U extends BaseEvent> void mockRemoteGetEventByEventId(U remoteEvent) {
    doReturn(List.of(remoteEvent.getGenericEventRecord()))
       .when(remoteAbstractTagService)
       .sendRemoteReq(anyString(), any(Filters.class));
  }
}
