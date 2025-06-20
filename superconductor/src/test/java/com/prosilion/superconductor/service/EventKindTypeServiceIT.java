package com.prosilion.superconductor.service;

import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.event.BadgeAwardDownvoteEvent;
import com.prosilion.nostr.event.BadgeAwardUpvoteEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.service.event.service.EventKindTypeServiceIF;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EventKindTypeServiceIT {
  private final EventKindServiceIF<GenericEventKindIF> eventKindService;
  private final EventKindTypeServiceIF<KindType, GenericEventKindTypeIF> eventKindTypeService;

  @Autowired
  public EventKindTypeServiceIT(
      EventKindServiceIF<GenericEventKindIF> eventKindService,
      EventKindTypeServiceIF<KindType, GenericEventKindTypeIF> eventKindTypeService) {
    this.eventKindTypeService = eventKindTypeService;
    this.eventKindService = eventKindService;
  }

  @Test
  void testUpvoteEvent() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();
    Identity upvotedUser = Identity.generateRandomIdentity();

    BadgeAwardUpvoteEvent typeBadgeAwardUpvoteEvent = new BadgeAwardUpvoteEvent(identity, upvotedUser, "UPVOTE event text content");
    GenericEventKindTypeIF genericEventKindIF = (GenericEventKindTypeIF) new EventDto(typeBadgeAwardUpvoteEvent).convertBaseEventToDto();
    eventKindTypeService.processIncomingEvent(genericEventKindIF);
  }

  @Test
  void testDownvoteEvent() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();
    Identity downvotedUser = Identity.generateRandomIdentity();

    BadgeAwardDownvoteEvent typeBadgeAwardDownvoteEvent = new BadgeAwardDownvoteEvent(identity, downvotedUser, "DOWN vote event text content");
    GenericEventKindTypeIF genericEventKindIF = (GenericEventKindTypeIF) new EventDto(typeBadgeAwardDownvoteEvent).convertBaseEventToDto();
    eventKindTypeService.processIncomingEvent(genericEventKindIF);
  }

  @Test
  void testTextNoteEvent() throws NostrException, NoSuchAlgorithmException {
    Identity identity = Identity.generateRandomIdentity();

    TextNoteEvent textNoteEvent = new TextNoteEvent(identity, "TEXT note event text content");
    eventKindService.processIncomingEvent(new EventDto(textNoteEvent).convertBaseEventToDto());
  }

//  @Test
//  void testAll() throws NostrException, NoSuchAlgorithmException {
//    Identity identity = Identity.generateRandomIdentity();
//    Identity upvotedUser = Identity.generateRandomIdentity();
//
//    BadgeAwardUpvoteEvent<KindType> typeBadgeAwardUpvoteEvent = new BadgeAwardUpvoteEvent<>(identity, upvotedUser, "UPVOTE event text content");
//    eventKindTypeService.processIncomingEvent(typeBadgeAwardUpvoteEvent);
//
//    Identity downvotedUser = Identity.generateRandomIdentity();
//
//    BadgeAwardDownvoteEvent<KindType> typeBadgeAwardDownvoteEvent = new BadgeAwardDownvoteEvent<>(identity, downvotedUser, "DOWN vote event text content");
//    eventKindTypeService.processIncomingEvent(typeBadgeAwardDownvoteEvent);
//
//    TextNoteEvent textNoteEvent = new TextNoteEvent(identity, "TEXT note event text content");
//    eventKindService.processIncomingEvent(new EventDto(textNoteEvent).convertBaseEventToDto());
//  }
}
