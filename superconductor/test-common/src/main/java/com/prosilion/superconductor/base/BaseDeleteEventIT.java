package com.prosilion.superconductor.base;

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
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.util.Factory;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import lombok.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseDeleteEventIT {
  private static final Identity identity = Identity.generateRandomIdentity();
  private final String relayUrl;
  private final String eventIdToDeleteId;
  private final String deletionEventId;

  public BaseDeleteEventIT(@NonNull String relayUrl) throws NostrException {
    this.relayUrl = relayUrl;
    NostrEventPublisher textNoteEventPublisher = new NostrEventPublisher(relayUrl);

    BaseEvent event = new TextNoteEvent(identity, Factory.lorumIpsum());
    this.eventIdToDeleteId = event.getId();

    EventMessage eventMessage = new EventMessage(event);
    assertTrue(
        textNoteEventPublisher
            .send(
                eventMessage)
            .getFlag());

    List<EventTag> eventDeletionTags = new ArrayList<>();
    eventDeletionTags.add(new EventTag(eventIdToDeleteId, relayUrl));

    BaseEvent deletionEvent = new DeletionEvent(identity, eventDeletionTags, Factory.lorumIpsum());
    this.deletionEventId = deletionEvent.getId();

    NostrEventPublisher deletionEventPublisher = new NostrEventPublisher(relayUrl);
    EventMessage deletionEventMessage = new EventMessage(deletionEvent);
    assertTrue(
        deletionEventPublisher
            .send(
                deletionEventMessage)
            .getFlag());
    log.debug("end");
//    nostrRelayService.disconnect();
  }

  @Test
  void testSingleTextNoteEventDeletion() throws NostrException {
    final String deletionSubmitterSubscriberId = Factory.generateRandomHex64String();

    EventFilter deletionEventFilter = new EventFilter(new GenericEventId(eventIdToDeleteId));

    ReqMessage deletionReqMessage = new ReqMessage(deletionSubmitterSubscriberId, new Filters(deletionEventFilter));
    List<BaseMessage> returnedDeletionMessagesShouldContainEose = new NostrSingleRequestService().send(deletionReqMessage, relayUrl);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedDeletionMessagesShouldContainEose);
    assertEquals(1, returnedDeletionMessagesShouldContainEose.size());
    assertEquals(1, returnedDeletionMessagesShouldContainEose.stream()
        .filter(EoseMessage.class::isInstance)
        .count());

    EventFilter eventFilter = new EventFilter(new GenericEventId(deletionEventId));

    final String subscriberId = Factory.generateRandomHex64String();
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    List<BaseMessage> returnedBaseMessages = new NostrSingleRequestService().send(reqMessage, relayUrl);
    List<EventIF> returnedEventIFs = getEventIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(deletionEventId)));

//    nostrRelayReqService.disconnect(this.relayUrl);
  }

  @Test
  void testCreateAnotherNoteAndConfirmItsNotDeleted() {
    BaseEvent event = new TextNoteEvent(identity, Factory.lorumIpsum());
    String secondEventShouldNotGetDeleted = event.getId();
    NostrEventPublisher nostrEventPublisher = new NostrEventPublisher(relayUrl);

    EventMessage eventMessage = new EventMessage(event);
    assertTrue(
        nostrEventPublisher
            .send(
                eventMessage)
            .getFlag());

    EventFilter eventFilter = new EventFilter(new GenericEventId(secondEventShouldNotGetDeleted));

    final String subscriberId = Factory.generateRandomHex64String();

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages = new NostrSingleRequestService().send(reqMessage, relayUrl);
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
