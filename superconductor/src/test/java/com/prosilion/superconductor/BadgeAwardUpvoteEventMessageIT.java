package com.prosilion.superconductor;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.dto.GenericEventKindTypeDto;
import com.prosilion.superconductor.util.BadgeAwardUpvoteEvent;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import com.prosilion.superconductor.util.TestKindType;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class BadgeAwardUpvoteEventMessageIT {
  private final NostrRelayService nostrRelayService;

  private final static Identity authorIdentity = Identity.generateRandomIdentity();
  private final PublicKey upvotedUserPubKey = Identity.generateRandomIdentity().getPublicKey();

  private final String eventId;

  @Autowired
  BadgeAwardUpvoteEventMessageIT(@NonNull NostrRelayService nostrRelayService) throws IOException, NostrException, NoSuchAlgorithmException {
    this.nostrRelayService = nostrRelayService;

    GenericEventKindTypeIF upvoteEvent =
        new GenericEventKindTypeDto(
            new BadgeAwardUpvoteEvent(
                authorIdentity,
                upvotedUserPubKey),
            TestKindType.UPVOTE)
            .convertBaseEventToGenericEventKindTypeIF();

    eventId = upvoteEvent.getId();

    EventMessage eventMessage = new EventMessage(upvoteEvent);
    assertTrue(
        this.nostrRelayService
            .send(
                eventMessage)
            .getFlag());
  }

  @Test
  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    List<GenericEventKindIF> returnedGenericEventKindIFs = getGenericEventKindIFs(
        nostrRelayService.send(
            new ReqMessage(
                subscriberId,
                new Filters(
                    new KindFilter(
                        Kind.BADGE_AWARD_EVENT),
                    new ReferencedPublicKeyFilter(
                        new PubKeyTag(
                            upvotedUserPubKey)),
                    new AddressTagFilter(
                        new AddressTag(
                            Kind.BADGE_DEFINITION_EVENT,
                            authorIdentity.getPublicKey(),
                            new IdentifierTag(
                                TestKindType.UPVOTE.getName())))))));

    log.debug("returned events:");
    log.debug("  {}", returnedGenericEventKindIFs);

    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
    assertTrue(returnedGenericEventKindIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));

    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedGenericEventKindIFs.getFirst()).getFirst();

    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
    assertEquals(TestKindType.UPVOTE.getName(), addressTag.getIdentifierTag().getUuid());
  }

  public static List<GenericEventKindIF> getGenericEventKindIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
