package com.prosilion.superconductor.supplier.local.abstracts;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.supplier.AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedBadgeDefinitionEventMessageSupplierLocalListIT extends AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT {
  protected AbstractCacheCuratedBadgeDefinitionEventMessageSupplierLocalListIT(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorRelayUrl, superconductorInstanceIdentity);
  }

  @Override
  protected List<BadgeDefinitionGenericEvent> createBadgeDefinitionGenericEventList() {
    return List.of(
       createBadgeDefinitionEventWithRelayTag(),
       createBadgeDefinitionEventWithoutRelayTag());
  }

  private @NonNull BadgeDefinitionGenericEvent createBadgeDefinitionEventWithRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       definitionEventRelay);
  }

  private @NonNull BadgeDefinitionGenericEvent createBadgeDefinitionEventWithoutRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);
  }
}
