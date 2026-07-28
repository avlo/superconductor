package com.prosilion.superconductor.base.curated;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedBadgeDefinitionEventRemoteSupplierIsSameRelayIT extends BaseCacheCuratedBadgeDefinitionEventMessageIT {
  Relay definitionEventRelay;

  protected AbstractCacheCuratedBadgeDefinitionEventRemoteSupplierIsSameRelayIT(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorRelayUrl, superconductorInstanceIdentity);
    this.definitionEventRelay = new Relay(superconductorRelayUrl);
  }

  @Override
  BadgeDefinitionGenericEvent createDefinitionEventContainingRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       definitionEventRelay);
  }

  @Override
  BadgeDefinitionGenericEvent createDefinitionEventWithoutRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);
  }
}
