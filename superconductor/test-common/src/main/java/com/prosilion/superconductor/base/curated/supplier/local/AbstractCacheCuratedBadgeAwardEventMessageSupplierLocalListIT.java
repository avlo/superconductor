package com.prosilion.superconductor.base.curated.supplier.local;

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
public abstract class AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalListIT extends AbstractBaseCacheCuratedBadgeAwardEventMessageListIT {
  protected AbstractCacheCuratedBadgeAwardEventMessageSupplierLocalListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String relayUrl) throws NostrException {
    super(superconductorInstanceIdentity, relayUrl, relayUrl);
  }

  @Override
  protected List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> createBadgeAwardEventList() {
    return List.of(
       createAwardEventContainingRelayTag(),
       createAwardEventWithoutRelayTag()
    );
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventContainingRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay(definitionEventRelayUrl)),
       new Relay(awardEventRelayUrl));
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createAwardEventWithoutRelayTag() {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag));
  }

  @Override
  public void overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(List<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> badgeAwardUpvoteEvents) {
    validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(badgeAwardUpvoteEvents);
  }
}
