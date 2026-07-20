package com.prosilion.superconductor;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.RemoteAbstractTagService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public abstract class CacheServiceTestFixture<T extends BaseEvent> {
  public static final Relay relay = new Relay("ws://localhost:5555");
  public static final String AWARD_UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
  public static final IdentifierTag upvoteIdentifierTag = new IdentifierTag(AWARD_UNIT_UPVOTE);
  public static final Identity upvoteDefnCreator =
//     Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");
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
    
    when(cacheServiceIF.getEventByEventId(anyString())).thenReturn(
       Optional.of(event.getGenericEventRecord()));
  }

  abstract T createEvent();
}
