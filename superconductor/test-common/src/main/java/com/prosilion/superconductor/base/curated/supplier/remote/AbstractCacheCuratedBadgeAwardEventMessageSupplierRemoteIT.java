package com.prosilion.superconductor.base.curated.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedBadgeAwardEventMessageIT;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedBadgeAwardEventMessageSupplierRemoteIT extends AbstractBaseCacheCuratedBadgeAwardEventMessageIT {
  protected AbstractCacheCuratedBadgeAwardEventMessageSupplierRemoteIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) throws NostrException {
    super(superconductorInstanceIdentity, definitionEventRelayUrl, awardEventRelayUrl);
  }

  @Override
  protected BadgeDefinitionGenericEvent createBadgeDefinitionGenericEvent() {
    return new BadgeDefinitionGenericEvent(
       superconductorInstanceIdentity,
       upvoteIdentifierTag,
       new Relay("ws://superconductor-app-two:5555"));
  }

  @Override
  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventContainingRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionUpvoteEvent,
       new Relay("ws://superconductor-app-three:5555"));
  }

  @Override
  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionUpvoteEvent);
  }
}
