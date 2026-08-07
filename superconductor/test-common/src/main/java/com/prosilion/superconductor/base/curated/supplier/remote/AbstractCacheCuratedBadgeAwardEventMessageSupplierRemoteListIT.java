package com.prosilion.superconductor.base.curated.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedBadgeAwardEventMessageListIT;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedBadgeAwardEventMessageSupplierRemoteListIT extends AbstractBaseCacheCuratedBadgeAwardEventMessageListIT {
  protected AbstractCacheCuratedBadgeAwardEventMessageSupplierRemoteListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity, definitionEventRelayUrl, awardEventRelayUrl);
  }

//  protected List<BadgeDefinitionGenericEvent> createBadgeDefinitionEvents() {
//    return List.of(
//       createBadgeDefinitionUpvoteEvent()

  /// /       ,
  /// /       createBadgeDefinitionDownvoteEvent()
//    );
//  }
  @Override
  protected List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> createBadgeAwardEventList() {
    return List.of(
       createAwardEventContainingRelayTag()
//       ,
//       createAwardEventWithoutRelayTag()
    );
  }

//  protected BadgeDefinitionGenericEvent createBadgeDefinitionUpvoteEvent() {
//    return new BadgeDefinitionGenericEvent(
//       upvoteDefnCreator,
//       upvoteIdentifierTag,
//       new Relay("ws://superconductor-app-two:5555"));
//  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventContainingRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay("ws://superconductor-app-two:5555")),
       new Relay("ws://superconductor-app-three:5555"));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag));
  }
}
