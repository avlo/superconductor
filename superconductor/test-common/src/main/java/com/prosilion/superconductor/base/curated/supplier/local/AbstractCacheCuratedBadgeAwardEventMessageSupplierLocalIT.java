package com.prosilion.superconductor.base.curated.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedBadgeAwardEventMessageIT;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalIT extends AbstractBaseCacheCuratedBadgeAwardEventMessageIT {
  protected AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String relayUrl) throws NostrException {
    super(superconductorInstanceIdentity, relayUrl, relayUrl);
  }

  @Override
  protected BadgeDefinitionGenericEvent createBadgeDefinitionUpvoteEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       new Relay(definitionEventRelayUrl));
  }

  @Override
  protected BadgeDefinitionGenericEvent createBadgeDefinitionDownvoteEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);
  }

  @Override
  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventContainingRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionUpvoteEvent,
       new Relay(awardEventRelayUrl));
  }

  @Override
  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionDownvoteEvent);
  }
}
