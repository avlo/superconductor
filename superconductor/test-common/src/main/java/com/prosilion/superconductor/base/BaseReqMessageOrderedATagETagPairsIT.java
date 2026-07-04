package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.util.Factory;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseReqMessageOrderedATagETagPairsIT {
  private final Identity defnCreator = // Identity.generateRandomIdentity();
     Identity.create("bbb4585483196998204846989544737603523651520600328805626488477202");

  private final Relay relay;
  private String eventId_1;
  private String eventId_2;
  private String eventId_3;
  List<BaseTag> tags_1;
  List<BaseTag> tags_2;
  List<BaseTag> tags_3;

  public BaseReqMessageOrderedATagETagPairsIT(@NonNull String relayUrl) {
    this.relay = new Relay(relayUrl);
    method_1();
    method_2();
    method_3();
  }

  protected void method_1() {
    NostrEventPublisher publisher = new NostrEventPublisher(relay.getUrl());

    PubKeyTag pubKeyTag_1 = new PubKeyTag(defnCreator.getPublicKey());
    HashtagTag hashtagTag_1 = new HashtagTag("hashtag_1");
    IdentifierTag uuid_1 = new IdentifierTag("UUID_1");

    AddressTag addressTag_1 = new AddressTag(
       Kind.TEXT_NOTE,
       Identity.generateRandomIdentity().getPublicKey(),
       uuid_1);

    EventTag eventTag_1 = new EventTag(Factory.generateRandomHex64String(), relay.getUrl());

    this.tags_1 = List.of(pubKeyTag_1, addressTag_1, eventTag_1, hashtagTag_1);
    TextNoteEvent event_1 = new TextNoteEvent(
       defnCreator,
       tags_1,
       "content_1");
    this.eventId_1 = event_1.getId();

    EventMessage eventMessage_1 = new EventMessage(event_1);
    assertTrue(
       publisher
          .send(
             eventMessage_1, Duration.ofMinutes(5))
          .getFlag());
  }

  public void method_2() {
    NostrEventPublisher publisher = new NostrEventPublisher(relay.getUrl());
    PubKeyTag pubKeyTag_2 = new PubKeyTag(Identity.generateRandomIdentity().getPublicKey());
    HashtagTag hashtagTag_2 = new HashtagTag("hashtag_2");
    IdentifierTag uuid_2 = new IdentifierTag("UUID-2");

    AddressTag addressTag_2 = new AddressTag(
       Kind.TEXT_NOTE,
       Identity.generateRandomIdentity().getPublicKey(),
       uuid_2);

    EventTag eventTag_2 = new EventTag(Factory.generateRandomHex64String(), relay.getUrl());

    this.tags_2 = List.of(pubKeyTag_2, addressTag_2, eventTag_2, hashtagTag_2);
    TextNoteEvent event_2 = new TextNoteEvent(
       defnCreator,
       Stream.concat(tags_1.stream(), tags_2.stream()).toList(),
       "content_2");

    EventMessage eventMessage_2 = new EventMessage(event_2);
    assertTrue(
       publisher
          .send(
             eventMessage_2)
          .getFlag());

    this.eventId_2 = event_2.getId();
  }

  public void method_3() {
    NostrEventPublisher publisher = new NostrEventPublisher(relay.getUrl());
    PubKeyTag pubKeyTag_3 = new PubKeyTag(Identity.generateRandomIdentity().getPublicKey());
    HashtagTag hashtagTag_3 = new HashtagTag("hashtag_3");
    IdentifierTag uuid_3 = new IdentifierTag("UUID-3");

    AddressTag addressTag_3 = new AddressTag(
       Kind.TEXT_NOTE,
       Identity.generateRandomIdentity().getPublicKey(),
       uuid_3);

    EventTag eventTag_3 = new EventTag(Factory.generateRandomHex64String(), relay.getUrl());

    this.tags_3 = List.of(addressTag_3, eventTag_3, hashtagTag_3, pubKeyTag_3);
    TextNoteEvent event_3 = new TextNoteEvent(
       defnCreator,
       Stream.concat(
          Stream.concat(
             tags_1.stream(),
             tags_2.stream()),
          tags_3.stream()).toList(),
       "content_3");

    EventMessage eventMessage_3 = new EventMessage(event_3);
    assertTrue(
       publisher
          .send(
             eventMessage_3)
          .getFlag());

    this.eventId_3 = event_3.getId();
  }

  @Test
  void testSingleATagETagPair() throws NostrException {
    System.out.printf("testSingleATagETagPair (_1) original tags order:\n %s%n", getTagStream(tags_1).collect(Collectors.joining("\n ")));
    List<BaseTag> tags = returnedBaseTags(eventId_1);
    System.out.printf("testSingleATagETagPair (_1) tags order:\n %s%n", getTagStream(tags).collect(Collectors.joining("\n ")));
    assertAddressTagsAreFollowedByEventTags(tags);
  }

  @Test
  void testTwoATagETagPairs() throws NostrException {
    System.out.printf("testTwoATagETagPairs (_2) original tags order:\n %s%n", getTagStream(tags_2).collect(Collectors.joining("\n ")));
    List<BaseTag> tags = returnedBaseTags(eventId_2);
    System.out.printf("testTwoATagETagPairs (_2) tags order:\n %s%n", getTagStream(tags).collect(Collectors.joining("\n ")));
    assertAddressTagsAreFollowedByEventTags(tags);
  }

  @Test
  void testTwoMixedPositionATagETagPairs() throws NostrException {
    List<String> tags1stream = getTagStream(tags_1).toList();
    List<String> tag2Stream = getTagStream(tags_2).toList();
    List<String> tag3Stream = getTagStream(tags_3).toList();
    System.out.printf("(_1) order: %s%n", String.join(", ", tags1stream));
    System.out.printf("(_2) order: %s%n", String.join(", ", tag2Stream));
    System.out.printf("(_3) order: %s%n", String.join(", ", tag3Stream));

    List<BaseTag> manual_concat_1_2_3 = Stream.concat(
       Stream.concat(
          tags_1.stream(),
          tags_2.stream()),
       tags_3.stream()).toList();
    System.out.printf("123  order:\n %s%n", getTagStream(manual_concat_1_2_3).collect(Collectors.joining(", ")));
    
    List<BaseTag> tags = returnedBaseTags(eventId_3);
    System.out.printf("(_3) order:\n %s%n", getTagStream(tags).collect(Collectors.joining(", ")));
    assertAddressTagsAreFollowedByEventTags(tags);
  }

  private void assertAddressTagsAreFollowedByEventTags(List<BaseTag> tags) {
    for (int i = 0; i < tags.size(); i++) {
      if (tags.get(i) instanceof AddressTag) {
        assertTrue(
           i + 1 < tags.size() && tags.get(i + 1) instanceof EventTag,
           "AddressTag at index " + i + " must be immediately followed by an EventTag. Tag order: "
              + getTagStream(tags).toList());
      }
    }
  }

  public static @NonNull Stream<String> getTagStream(List<BaseTag> tags) {
    int maxTagNameLength =
       tags.stream().mapToInt(tag -> 11).max().orElse(0);
    int maxHashCodeLength =
       tags.stream().mapToInt(tag -> 11).max().orElse(0);
//    return tags.stream().map(tag ->
//       String.format("%s-%s",
//          tag.getClass().getSimpleName(), tag.hashCode()));
    return tags.stream().map(tag ->
       String.format("%-" + maxTagNameLength + "s : %" + maxHashCodeLength + "s",
          tag.getClass().getSimpleName(), tag.hashCode()));
  }

  private List<BaseTag> returnedBaseTags(String eventId) {
    List<EventIF> returnedEventIFs = BaseTextNoteEventMessageIT.getEventIFs(
       new NostrSingleRequestService()
          .send(
             new ReqMessage(Factory.generateRandomHex64String(),
                new Filters(new EventFilter(new GenericEventId(eventId)))),
             relay.getUrl()));
    assertEquals(1, returnedEventIFs.stream().map(EventIF::getId).toList().size());
    assertTrue(returnedEventIFs.stream().map(EventIF::getId).toList().contains(eventId));
    return returnedEventIFs.getFirst().getTags();
  }

//  @Test
//  void complexVariant() throws ParseException {
//    NostrEventPublisher publisher = new NostrEventPublisher(relay.getUrl());
//
//    String PLATFORM = "afterimage";
//    String proof = "proof";
//    ExternalIdentityTag BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG = new ExternalIdentityTag(
//       PLATFORM,
//       "badge_definition_reputation",
//       proof);
//
//    String REPUTATION = "TEST_REPUTATION";
//    String UNIT_UPVOTE = "TEST_UNIT_UPVOTE";
//    String PLUS_ONE_FORMULA = "+1";
//
//    IdentifierTag reputationIdentifierTag = new IdentifierTag(REPUTATION);
//    IdentifierTag upvoteIdentifierTag = new IdentifierTag(UNIT_UPVOTE);
//    Identity aImgIdentity = Identity.generateRandomIdentity();
//
//    BadgeDefinitionGenericEvent awardUpvoteDefinitionEvent = new BadgeDefinitionGenericEvent(aImgIdentity, upvoteIdentifierTag, relay);
//    FormulaEvent plusOneFormulaEvent = new FormulaEvent(aImgIdentity, upvoteIdentifierTag, relay, awardUpvoteDefinitionEvent, PLUS_ONE_FORMULA);
//
//    BadgeDefinitionReputationEvent badgeDefinitionReputationEventPlusOneFormula = new BadgeDefinitionReputationEvent(
//       aImgIdentity,
//       defnCreator.getPublicKey(),
//       reputationIdentifierTag,
//       relay,
//       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
//       plusOneFormulaEvent);
//
//    BadgeSetsEvent event = new BadgeSetsEvent(
//       defnCreator,
//       badgeDefinitionReputationEventPlusOneFormula,
//       );
//
//    EventMessage eventMessage = new EventMessage(event);
//    assertTrue(
//       publisher
//          .send(
//             eventMessage)
//          .getFlag());
//  }
}
