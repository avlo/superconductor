package com.prosilion.superconductor.supplier.local.abstracts;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.supplier.AbstractBaseCacheCuratedBadgeAwardEventMessageListIT;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.prosilion.nostr.util.Util.generateRandomHex64String;
import static com.prosilion.superconductor.BaseCacheFollowSetsEventServiceIT.getEventIFs;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalListIT extends AbstractBaseCacheCuratedBadgeAwardEventMessageListIT {
  protected AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String relayUrl) throws NostrException {
    super(superconductorInstanceIdentity, relayUrl, relayUrl);
  }

  @Override
  protected List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> createBadgeAwardEventList() {
    return List.of(
       create_AwardEventWithRelayTag_DefinitionEventWithRelayTag(),
       create_AwardEventWithRelayTag_DefinitionEventWithoutRelayTag(),
       create_AwardEventWithoutRelayTag_DefinitionEventWithRelayTag(),
       create_AwardEventWithoutRelayTag_DefinitionEventWithoutRelayTag());

//  including below event instances exposes potential classes/clobberings as described below.  
//    consider re-adding as time permits

//  createAwardEventWithRelayTagDefinitionEventWithRelayTag(),
//  createAwardEventWithoutRelayTagDefinitionEventWithoutRelayTag(),
//  createAwardEventWithRelayTagDefinitionEventWithoutRelayTag(),
//  createAwardEventWithoutRelayTagDefinitionEventWithRelayTag()

//  DESCRIPTIONS
//  returnedCuratedBadgeDefinitionEvents.stream().map(EventIF::getId).collect(Collectors.joining(",\n"))
//  
//  349f54aa57d8e3939a48474d13de11b1f75b597246fb2fecd215861c0c3dbf13, match
//  6c8785c5290ebfd2362c215e7233d14cf5e909abc9dcf8cef80243534a226ded, match
//  68377bdddd734cab5ff234ffc6d5b58624abd9d95d8174692e788937854d92c6, missing from below
//  9e1f7ce7824912ca420b77a429fbf0ab3df68a227be76f081511f5a5d1fe760b, missing from below
//  
//  ~~~~
//  
//  returnedCuratedBadgeAwardEvents.stream().map(event -> event.requireFirstTag(IdentifierTag.class).getUuid()).collect(Collectors.joining(",\n"))
//  
//  349f54aa57d8e3939a48474d13de11b1f75b597246fb2fecd215861c0c3dbf13, match
//  6c8785c5290ebfd2362c215e7233d14cf5e909abc9dcf8cef80243534a226ded, match
//  349f54aa57d8e3939a48474d13de11b1f75b597246fb2fecd215861c0c3dbf13, duplicate of first match and missing as per above (should be 68377bdddd734cab5ff234ffc6d5b58624abd9d95d8174692e788937854d92c6)
//  349f54aa57d8e3939a48474d13de11b1f75b597246fb2fecd215861c0c3dbf13, duplicate of first match and missing as per above (should be 9e1f7ce7824912ca420b77a429fbf0ab3df68a227be76f081511f5a5d1fe760b)
//  
//  
//  ~~~~
//  this.badgeAwardEventList.ids.stream
//  
//  70e35ea80756b2c51b9ab8175e86d48ec9b2f1aa04c968d6abd3d292bf830aca
//  b43639a9a89a07922af963c035645840cbee4c2aad3a88715ed618ae8b837e4b
//  f3dd3099b36f832374f0d5c1d36df3d9749f3f01b4c0e83ca88243e17bd54dca
//  df2ad7db69dbeaeea6f64e93f5af5ca24cd6006795985801acc89cc211b51c50
//  
//  
//  ~~~~
//  returnedCuratedBadgeAwardEvents.stream().map(EventIF::getId).collect(Collectors.joining(",\n"))
//  c9097c66a9ffb6b54d730fe9a81e7f6ff393c7ec5b24235836add25ebc3b3ebe,
//  690dd3e939dba7426b1752e6ee999dc6a75ae6fc83fe9f15a4450078c67c1e2e,
//  4dbcd4ae2d686b90abe13fc1d7bd40be30ce30395721cb9c529b09ca4de34667,
//  f3d99dca62e39e68bd54dcc5b95f541ee5a681d25fc7742b4aef9d1e14620fe9
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> create_AwardEventWithRelayTag_DefinitionEventWithRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_1");
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag,
          definitionEventRelayUrl),
       awardEventRelayUrl);
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> create_AwardEventWithRelayTag_DefinitionEventWithoutRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_2");
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag),
       definitionEventRelayUrl); // <---- if present (for BadgeAwardEvent), SC should implicitly use it iff BadgeDefinitionGenericEvent hasn't specified a relay   
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> create_AwardEventWithoutRelayTag_DefinitionEventWithRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_3");
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag,
          definitionEventRelayUrl));
  }


  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> create_AwardEventWithoutRelayTag_DefinitionEventWithoutRelayTag() {
//    IdentifierTag identifierTag = upvoteIdentifierTag;
    IdentifierTag identifierTag = new IdentifierTag("BDG_DEF_UNIT_UP_4");
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          identifierTag));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithRelayTagDefinitionEventWithRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay(definitionEventRelayUrl)),
       new Relay(awardEventRelayUrl));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTagDefinitionEventWithRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay(definitionEventRelayUrl)));
  }


  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithRelayTagDefinitionEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag),
       new Relay(awardEventRelayUrl));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTagDefinitionEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag));
  }

  protected void validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardUpvoteEvents) {
    List<EventIF> sanityCheckReturnedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned BadgeDefinitionEvents:");
    log.debug("  {}", sanityCheckReturnedBadgeDefinitionEvents);

    Set<String> sanityCheckCurationSetsBadgeDefinitionEventIds = sanityCheckReturnedBadgeDefinitionEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).collect(Collectors.toSet());

    assertTrue(sanityCheckCurationSetsBadgeDefinitionEventIds.stream().anyMatch(
       badgeAwardGenericEventList.stream().map(BadgeAwardAbstractEvent::getBadgeDefinitionEvent)
          .map(BaseEvent::getId).toList()::contains));

    badgeAwardUpvoteEvents.forEach(badgeAwardUpvoteEvent ->
       assertTrue(
          new NostrEventPublisher(awardEventRelayUrl).send(
             new EventMessage(badgeAwardUpvoteEvent), Duration.ofSeconds(10)).getFlag()));
  }
}
