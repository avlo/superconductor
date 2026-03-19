package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.ClassifiedListingEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.event.internal.ClassifiedListing;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PriceTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.tag.SubjectTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.subdivisions.client.reactive.NostrRequestService;
import com.prosilion.superconductor.base.util.NostrComprehensiveRelayService;
import com.prosilion.superconductor.util.Factory;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseClassifiedListingEventMessageIT {
  private final String relayUrl;
  private final PublicKey senderPubkey;

  public static final String PTAG_HEX = "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76985";

  public static final PubKeyTag P_TAG = new PubKeyTag(new PublicKey(PTAG_HEX));
  private final EventTag E_TAG;
  public static final IdentifierTag identifierTag = new IdentifierTag("ClassifiedListingUuid");

  public static final String SUBJECT = "Classified Listing Test Subject Tag";
  public static final SubjectTag SUBJECT_TAG = new SubjectTag(SUBJECT);
  public static final GeohashTag G_TAG = new GeohashTag("Classified Listing Test Geohash Tag");
  public static final HashtagTag T_TAG = new HashtagTag("Classified Listing Test Hashtag Tag");

  public static final BigDecimal NUMBER = new BigDecimal("2.71");
  public static final String FREQUENCY = "NANOSECOND";
  public static final String CURRENCY = "BTC";
  public static final PriceTag PRICE_TAG = new PriceTag(NUMBER, CURRENCY, FREQUENCY);

  public static final String CLASSIFIED_LISTING_TITLE = "classified listing title";
  public static final String CLASSIFIED_LISTING_SUMMARY = "classified listing summary";
  public static final String CLASSIFIED_LISTING_LOCATION = "classified listing location";

  private static final Identity identity = Identity.generateRandomIdentity();
  private final String eventId;
  private static final String globalSubscriberId = Factory.generateRandomHex64String(); // global subscriber UUID
  private final String content;
  Duration requestTimeoutDuration;

  public BaseClassifiedListingEventMessageIT(@NonNull String relayUrl, @NonNull Duration requestTimeoutDuration) throws IOException, NostrException {
    this.relayUrl = relayUrl;
    this.requestTimeoutDuration = requestTimeoutDuration;
    Relay relay = new Relay(relayUrl);
    NostrComprehensiveRelayService nostrComprehensiveRelayService = new NostrComprehensiveRelayService(relayUrl, requestTimeoutDuration);
    this.content = Factory.lorumIpsum(getClass());

    senderPubkey = new PublicKey(identity.getPublicKey().toString());

    TextNoteEvent textNoteEvent = new TextNoteEvent(identity, List.of(new RelayTag(relay)), "text note event content");

    EventMessage textNoteEventMessage = new EventMessage(textNoteEvent);
    assertTrue(
        nostrComprehensiveRelayService
            .send(
                textNoteEventMessage)
            .getFlag());

    E_TAG = new EventTag(textNoteEvent.getId(), relayUrl);

    List<BaseTag> tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);

    ClassifiedListing classifiedListing = new ClassifiedListing(
        CLASSIFIED_LISTING_TITLE, CLASSIFIED_LISTING_SUMMARY, PRICE_TAG, CLASSIFIED_LISTING_LOCATION);

    BaseEvent classifiedListingEvent = new ClassifiedListingEvent(identity, Kind.CLASSIFIED_LISTING_ACTIVE, identifierTag, relay, classifiedListing, tags, content);
    this.eventId = classifiedListingEvent.getId();

    EventMessage classifiedListingEventMessage = new EventMessage(classifiedListingEvent);
    assertTrue(
        nostrComprehensiveRelayService
            .send(
                classifiedListingEventMessage)
            .getFlag());
    log.debug("done");
  }

  @Test
  void testReqSingleSubscriberFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    AuthorFilter authorFilter = new AuthorFilter(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    NostrRequestService nostrRelayService = new NostrRequestService();
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage, relayUrl);
    List<EventIF> returnedEventIFs = getEventIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedBaseMessages);
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqTwoSubscribersFilteredByEventAndAuthorViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    AuthorFilter authorFilter = new AuthorFilter(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter, authorFilter));
    NostrRequestService nostrRelayService = new NostrRequestService();
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage, relayUrl);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    log.debug("okMessage to UniqueSubscriberId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter, authorFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2, relayUrl);
    List<EventIF> returnedEvents2 = getEventIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqFilteredByEventId() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(eventId));
    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(eventFilter));

    NostrRequestService nostrRelayService = new NostrRequestService();
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage, relayUrl);

    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    log.debug("okMessage to testReqFilteredByEventId:");
    log.debug("  " + returnedEvents);
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event -> event.getContent().equals(content)));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(eventFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2, relayUrl);
    List<EventIF> returnedEvents2 = getEventIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getContent().equals(content)));
  }

  @Test
  void testReqFilteredByAuthor() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    AuthorFilter authorFilter = new AuthorFilter(identity.getPublicKey());

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));
    NostrRequestService nostrRelayService = new NostrRequestService();
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage, relayUrl);

    log.debug("okMessage to testReqFilteredByAuthor:");
    log.debug("  " + returnedBaseMessages);
    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);

    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));

    ReqMessage reqMessage2 = new ReqMessage(globalSubscriberId, new Filters(authorFilter));
    List<BaseMessage> returnedBaseMessages2 = nostrRelayService.send(reqMessage2, relayUrl);
    List<EventIF> returnedEvents2 = getEventIFs(returnedBaseMessages2);

    log.debug("okMessage:");
    log.debug("  " + returnedEvents2);
    assertTrue(returnedEvents2.stream().anyMatch(event -> event.getPublicKey().equals(identity.getPublicKey())));
  }

  @Test
  void testReqNonMatchingEvent() throws JsonProcessingException, NostrException {
    String nonMatchingSubscriberId = Factory.generateRandomHex64String();
    String nonMatchingEventId = Factory.generateRandomHex64String();

    EventFilter eventFilter = new EventFilter(new GenericEventId(nonMatchingEventId));

    ReqMessage reqMessage = new ReqMessage(nonMatchingSubscriberId, new Filters(eventFilter));
    NostrRequestService nostrRelayService = new NostrRequestService();
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage, relayUrl);

    List<EventIF> returnedEvents = getEventIFs(returnedBaseMessages);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);
    assertEquals(1, returnedBaseMessages.size());
    assertTrue(returnedBaseMessages.stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast)
        .findAny().isPresent());
    assertTrue(returnedEvents.isEmpty());
  }

  public static List<EventIF> getEventIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
