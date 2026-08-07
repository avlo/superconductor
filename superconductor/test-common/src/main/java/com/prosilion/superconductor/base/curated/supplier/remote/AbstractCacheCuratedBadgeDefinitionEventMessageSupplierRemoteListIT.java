package com.prosilion.superconductor.base.curated.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT;
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
  protected List<BadgeDefinitionGenericEvent> createDefinitionEvents() {
    return List.of(
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay("ws://superconductor-app-two:5555")),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag)
    );
  }
}
