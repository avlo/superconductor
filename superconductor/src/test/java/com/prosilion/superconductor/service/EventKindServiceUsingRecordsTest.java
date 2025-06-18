package com.prosilion.superconductor.service;

import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.event.BadgeAwardDownvoteEvent;
import com.prosilion.nostr.event.BadgeAwardUpvoteEvent;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.service.event.service.EventKindTypeServiceIF;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EventKindServiceUsingRecordsTest {
  private static final Log log = LogFactory.getLog(EventKindServiceUsingRecordsTest.class);

  private final EventKindTypeServiceIF<Type, AbstractBadgeAwardEvent<Type>> eventKindTypeService;
  private final EventKindServiceIF<GenericEventDtoIF> eventKindService;

  @Autowired
  public EventKindServiceUsingRecordsTest(
      EventKindTypeServiceIF<Type, AbstractBadgeAwardEvent<Type>> eventKindTypeService,
      EventKindServiceIF<GenericEventDtoIF> eventKindService) {
    this.eventKindTypeService = eventKindTypeService;
    this.eventKindService = eventKindService;
  }

  @Test
  void testUpvoteEvent() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();
    Identity upvotedUser = Identity.generateRandomIdentity();

    BadgeAwardUpvoteEvent<Type> typeBadgeAwardUpvoteEvent = new BadgeAwardUpvoteEvent<>(identity, upvotedUser, "UPVOTE event text content");
    eventKindTypeService.processIncomingEvent(typeBadgeAwardUpvoteEvent);
  }

  @Test
  void testDownvoteEvent() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();
    Identity downvotedUser = Identity.generateRandomIdentity();

    BadgeAwardDownvoteEvent<Type> typeBadgeAwardDownvoteEvent = new BadgeAwardDownvoteEvent<>(identity, downvotedUser, "DOWN vote event text content");
    eventKindTypeService.processIncomingEvent(typeBadgeAwardDownvoteEvent);
  }

  @Test
  void testTextNoteEvent() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();

    TextNoteEvent textNoteEvent = new TextNoteEvent(identity, "TEXT note event text content");
    eventKindService.processIncomingEvent(new EventDto(textNoteEvent).convertBaseEventToDto());
  }

  @Test
  void testAll() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();
    Identity upvotedUser = Identity.generateRandomIdentity();

    BadgeAwardUpvoteEvent<Type> typeBadgeAwardUpvoteEvent = new BadgeAwardUpvoteEvent<>(identity, upvotedUser, "UPVOTE event text content");
    eventKindTypeService.processIncomingEvent(typeBadgeAwardUpvoteEvent);

    Identity downvotedUser = Identity.generateRandomIdentity();

    BadgeAwardDownvoteEvent<Type> typeBadgeAwardDownvoteEvent = new BadgeAwardDownvoteEvent<>(identity, downvotedUser, "DOWN vote event text content");
    eventKindTypeService.processIncomingEvent(typeBadgeAwardDownvoteEvent);

    TextNoteEvent textNoteEvent = new TextNoteEvent(identity, "TEXT note event text content");
    eventKindService.processIncomingEvent(new EventDto(textNoteEvent).convertBaseEventToDto());
  }
}
