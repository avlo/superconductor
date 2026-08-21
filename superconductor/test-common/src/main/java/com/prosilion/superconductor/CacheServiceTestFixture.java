package com.prosilion.superconductor;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.RemoteAbstractTagService;
import com.prosilion.superconductor.base.BaseTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

public abstract class CacheServiceTestFixture<T extends BaseEvent> extends BaseTestFixtures {
  @Mock
  protected CacheServiceIF cacheServiceIF;
  @Mock
  protected RemoteAbstractTagService remoteAbstractTagService;

  protected T event;
  protected String eventId;

  public CacheServiceTestFixture() {
    super(Identity.create("fa11661b5f43c8f18f11861b4d553c47337dac9e351083b27320e311b7b324ac"));
  }


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
