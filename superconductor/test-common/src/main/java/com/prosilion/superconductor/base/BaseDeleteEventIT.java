package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.util.NostrRelayReqService;
import com.prosilion.superconductor.base.util.NostrRelayService;
import com.prosilion.superconductor.util.Factory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseDeleteEventIT {
  private static final Identity identity = Identity.generateRandomIdentity();
  private final String relayUrl;
  private final String eventIdToDeleteId;
  private final String deletionEventId;

  public BaseDeleteEventIT(@NonNull String relayUrl) throws IOException, NostrException {
    this.relayUrl = relayUrl;
    NostrRelayService nostrRelayService = new NostrRelayService(relayUrl);

    BaseEvent event = new TextNoteEvent(identity, Factory.lorumIpsum());
    this.eventIdToDeleteId = event.getId();

    EventMessage eventMessage = new EventMessage(event);
    assertTrue(
        nostrRelayService
            .send(
                eventMessage)
            .getFlag());

    List<EventTag> eventDeletionTags = new ArrayList<>();
    eventDeletionTags.add(new EventTag(eventIdToDeleteId));

    BaseEvent deletionEvent = new DeletionEvent(identity, eventDeletionTags, Factory.lorumIpsum());
    this.deletionEventId = deletionEvent.getId();

    EventMessage deletionEventMessage = new EventMessage(deletionEvent);
    assertTrue(
        nostrRelayService
            .send(
                deletionEventMessage)
            .getFlag());
    log.debug("end");
//    nostrRelayService.disconnect();
  }

  @Test
  void testSingleTextNoteEventDeletion() throws JsonProcessingException, NostrException {
    final String deletionSubmitterSubscriberId = Factory.generateRandomHex64String();

    EventFilter deletionEventFilter = new EventFilter(new GenericEventId(eventIdToDeleteId));

    NostrRelayReqService nostrRelayReqService = new NostrRelayReqService();
    ReqMessage deletionReqMessage = new ReqMessage(deletionSubmitterSubscriberId, new Filters(deletionEventFilter));
    List<BaseMessage> returnedDeletionMessagesShouldContainEose = nostrRelayReqService.send(deletionReqMessage, this.relayUrl);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedDeletionMessagesShouldContainEose);
    assertEquals(1, returnedDeletionMessagesShouldContainEose.size());
    assertEquals(1, returnedDeletionMessagesShouldContainEose.stream()
        .filter(EoseMessage.class::isInstance)
        .count());

    EventFilter eventFilter = new EventFilter(new GenericEventId(deletionEventId));

    final String subscriberId = Factory.generateRandomHex64String();
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayReqService.send(reqMessage, this.relayUrl);
    List<EventIF> returnedEventIFs = getEventIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(deletionEventId)));

//    nostrRelayReqService.disconnect(this.relayUrl);
  }

  @Test
  void testCreateAnotherNoteAndConfirmItsNotDeleted() throws IOException {
    BaseEvent event = new TextNoteEvent(identity, Factory.lorumIpsum());
    String secondEventShouldNotGetDeleted = event.getId();

    NostrRelayService nostrRelayService = new NostrRelayService(this.relayUrl);

    EventMessage eventMessage = new EventMessage(event);
    assertTrue(
        nostrRelayService
            .send(
                eventMessage)
            .getFlag());

    EventFilter eventFilter = new EventFilter(new GenericEventId(secondEventShouldNotGetDeleted));

    final String subscriberId = Factory.generateRandomHex64String();

    NostrRelayReqService nostrRelayReqService = new NostrRelayReqService();
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages = nostrRelayReqService.send(reqMessage, this.relayUrl);
    List<EventIF> returnedEventIFs = getEventIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedEventIFs.stream().anyMatch(secondEvent -> secondEvent.getId().equals(secondEventShouldNotGetDeleted)));
  }

  public static List<EventIF> getEventIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
