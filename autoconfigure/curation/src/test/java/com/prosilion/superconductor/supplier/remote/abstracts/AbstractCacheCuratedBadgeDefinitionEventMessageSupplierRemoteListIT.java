package com.prosilion.superconductor.supplier.remote.abstracts;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.supplier.AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedBadgeDefinitionEventMessageSupplierRemoteListIT extends AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT {
  protected AbstractCacheCuratedBadgeDefinitionEventMessageSupplierRemoteListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl) throws NostrException {
    super(definitionEventRelayUrl, superconductorInstanceIdentity);
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
       new Relay("ws://superconductor-app-three:5555"));
  }

  private @NonNull BadgeDefinitionGenericEvent createBadgeDefinitionEventWithoutRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);
  }
}
