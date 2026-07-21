package com.prosilion.superconductor;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
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
  public static final Relay relay = new Relay("ws://localhost:5555");
  public static final String AWARD_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);

  protected final Identity aImgIdentity =
     Identity.create("fa11661b5f43c8f18f11861b4d553c47337dac9e351083b27320e311b7b324ac");

  protected final Identity submitter =
//     Identity.generateRandomIdentity();
     Identity.create("aaa4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity recipient =
//     Identity.generateRandomIdentity();
     Identity.create("ccc4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity formulaCreator =
//     Identity.generateRandomIdentity();
     Identity.create("ddd4585483196998204846989544737603523651520600328805626488477202");

  protected final Identity repDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("eee4585483196998204846989544737603523651520600328805626488477202");

  @Mock
  CacheServiceIF cacheServiceIF;
  @Mock
  RemoteAbstractTagService remoteAbstractTagService;

  T event;
  String eventId;

  @BeforeEach
  void setUp() {
    this.event = createEvent();
    this.eventId = event.getId();
  }

  abstract T createEvent();

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
