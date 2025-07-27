package com.prosilion.superconductor.h2db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.h2db.util.NostrRelayService;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class DeleteEventH2dbIT {
  private final NostrRelayService nostrRelayService;

  private final static Identity identity = Identity.generateRandomIdentity();
  private final String eventIdToDelete;
  private final String deletionEvent;

  @Autowired
  DeleteEventH2dbIT(@NonNull NostrRelayService nostrRelayService) throws IOException, NostrException, NoSuchAlgorithmException {
    this.nostrRelayService = nostrRelayService;

    BaseEvent event = new TextNoteEvent(identity, Factory.lorumIpsum());
    this.eventIdToDelete = event.getId();

    GenericEventKindIF genericEventDtoIF = new GenericEventKindDto(event).convertBaseEventToGenericEventKindIF();
    EventMessage eventMessage = new EventMessage(genericEventDtoIF);
    assertTrue(
        this.nostrRelayService
            .send(
                eventMessage)
            .getFlag());

    List<EventTag> eventDeletionTags = new ArrayList<>();
    eventDeletionTags.add(new EventTag(eventIdToDelete));

    BaseEvent deletionEvent = new DeletionEvent(identity, eventDeletionTags, Factory.lorumIpsum());
    this.deletionEvent = deletionEvent.getId();

    GenericEventKindIF genericDeleteEventDtoIF = new GenericEventKindDto(deletionEvent).convertBaseEventToGenericEventKindIF();
    EventMessage deletionEventMessage = new EventMessage(genericDeleteEventDtoIF);
    assertTrue(
        this.nostrRelayService
            .send(
                deletionEventMessage)
            .getFlag());
  }

  @Test
  void testReqSingleSubscriberFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String deletionSubmitterSubscriberId = Factory.generateRandomHex64String();

    EventFilter deletionEventFilter = new EventFilter(new GenericEventId(eventIdToDelete));

    ReqMessage deletionReqMessage = new ReqMessage(deletionSubmitterSubscriberId, new Filters(deletionEventFilter));
    List<BaseMessage> returnedDeltionMessagesShouldBeEmpty = nostrRelayService.send(deletionReqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFsShouldBeEmpty = getGenericEventKindIFs(returnedDeltionMessagesShouldBeEmpty);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedDeltionMessagesShouldBeEmpty);
    assertTrue(returnedGenericEventKindIFsShouldBeEmpty.isEmpty());

    EventFilter eventFilter = new EventFilter(new GenericEventId(deletionEvent));

    final String subscriberId = Factory.generateRandomHex64String();
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(deletionEvent)));
  }

  public static List<GenericEventKindIF> getGenericEventKindIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
