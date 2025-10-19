//package com.prosilion.superconductor.sqlite;
//
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.enums.KindTypeIF;
//import com.prosilion.nostr.event.BadgeDefinitionEvent;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.GenericEventKindTypeIF;
//import com.prosilion.nostr.filter.Filterable;
//import com.prosilion.nostr.filter.Filters;
//import com.prosilion.nostr.filter.event.KindFilter;
//import com.prosilion.nostr.filter.tag.AddressTagFilter;
//import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
//import com.prosilion.nostr.message.BaseMessage;
//import com.prosilion.nostr.message.EventMessage;
//import com.prosilion.nostr.message.ReqMessage;
//import com.prosilion.nostr.tag.AddressTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.tag.PubKeyTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.superconductor.base.service.event.type.SuperconductorKindType;
//import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
//import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
//import com.prosilion.superconductor.sqlite.util.BadgeAwardUpvoteEvent;
//import com.prosilion.superconductor.sqlite.util.Factory;
//import com.prosilion.superconductor.sqlite.util.NostrRelayService;
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.lang.NonNull;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//public class BadgeAwardUpvoteEventMessageIT {
//  private final NostrRelayService nostrRelayService;
//
//  private final Identity authorIdentity = Identity.generateRandomIdentity();
//  private final PublicKey upvotedUserPubKey = Identity.generateRandomIdentity().getPublicKey();
//  private final Identity superconductorInstanceIdentity;
//
//  private final String eventId;
//
//  @Autowired
//  BadgeAwardUpvoteEventMessageIT(
//      @NonNull NostrRelayService nostrRelayService,
//      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionEvent badgeDefinitionUpvoteEvent,
//      @NonNull Identity superconductorInstanceIdentity) throws IOException, NostrException, NoSuchAlgorithmException {
//    this.nostrRelayService = nostrRelayService;
//    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
//
//    GenericEventKindTypeIF upvoteEvent =
//        new GenericEventKindTypeDto(
//            new BadgeAwardUpvoteEvent(
//                authorIdentity,
//                upvotedUserPubKey,
//                badgeDefinitionUpvoteEvent),
//            SuperconductorKindType.UPVOTE)
//            .convertBaseEventToEventIF();
//
//    eventId = upvoteEvent.getId();
//
//    EventMessage eventMessage = new EventMessage(upvoteEvent);
//    assertTrue(
//        this.nostrRelayService
//            .send(
//                eventMessage)
//            .getFlag());
//  }
//
//  @Test
//  void testValidExistingEventThenAfterImageReputationRequest() throws IOException, NostrException {
//    final String subscriberId = Factory.generateRandomHex64String();
//
//    List<EventIF> returnedEventIFs = getEventIFs(
//        nostrRelayService.send(
//            new ReqMessage(
//                subscriberId,
//                new Filters(
//                    new KindFilter(
//                        Kind.BADGE_AWARD_EVENT),
//                    new ReferencedPublicKeyFilter(
//                        new PubKeyTag(
//                            upvotedUserPubKey)),
//                    new AddressTagFilter(
//                        new AddressTag(
//                            Kind.BADGE_DEFINITION_EVENT,
//                            superconductorInstanceIdentity.getPublicKey(),
//                            new IdentifierTag(
//                                SuperconductorKindType.UPVOTE.getName())))))));
//
//    log.debug("returned events:");
//    log.debug("  {}", returnedEventIFs);
//
//    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getId().equals(eventId)));
//    assertTrue(returnedEventIFs.stream().anyMatch(event -> event.getPublicKey().equals(authorIdentity.getPublicKey())));
//
//    AddressTag addressTag = Filterable.getTypeSpecificTags(AddressTag.class, returnedEventIFs.getFirst()).getFirst();
//
//    assertEquals(Kind.BADGE_DEFINITION_EVENT, addressTag.getKind());
//    assertEquals(SuperconductorKindType.UPVOTE.getName(), addressTag.getIdentifierTag().getUuid());
//  }
//
//  public static List<EventIF> getEventIFs(List<BaseMessage> returnedBaseMessages) {
//    return returnedBaseMessages.stream()
//        .filter(EventMessage.class::isInstance)
//        .map(EventMessage.class::cast)
//        .map(EventMessage::getEvent)
//        .toList();
//  }
//
//  public static record GenericEventKindTypeDto(BaseEvent event, KindTypeIF kindType) {
//  
//    public EventEntityIF convertDtoToEntity() {
//      return new GenericEventKindDto(event).convertDtoToEntity();
//    }
//  
//    public EventIF convertBaseEventToEventIF() {
//      return new GenericEventKindType(
//          new GenericEventKindDto(event).convertBaseEventToEventIF(),
//          kindType);
//    }
//  }
//}
