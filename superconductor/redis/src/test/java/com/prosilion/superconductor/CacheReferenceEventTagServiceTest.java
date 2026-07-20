package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.RemoteAbstractTagService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class CacheReferenceEventTagServiceTest {
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

  private CacheReferenceEventTagService cacheReferenceEventTagService;

  private String eventId;

  @BeforeEach
  void setUp() {
    GenericEventRecord genericEventRecord = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay).asGenericEventRecord();
    this.eventId = genericEventRecord.getId();

    when(cacheServiceIF.getEventByEventId(anyString())).thenReturn(
       Optional.of(genericEventRecord));

    cacheReferenceEventTagService =
       new CacheReferenceEventTagService(cacheServiceIF, remoteAbstractTagService);
  }

  @Test
  @Order(1)
  void testGetEventByEventId() {
//  invoke...
    String actualEventIdViaEventTagService = cacheReferenceEventTagService.getEvent(eventId, relay).orElseThrow().getId();
    assertEquals(eventId, actualEventIdViaEventTagService);

//  ... verify invocation
    verify(cacheServiceIF).getEventByEventId(eventId);
  }

  @Test
  @Order(2)
  void testGetEventByEventIdCalledOnce() {
    cacheReferenceEventTagService.getEvent(eventId, relay);
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(anyString());
  }
}
