package com.prosilion.superconductor.base.curated.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class AbstractCacheCuratedBadgeDefinitionEventMessageSupplierLocalListIT extends AbstractBaseCacheCuratedBadgeDefinitionEventMessageListIT {
  protected AbstractCacheCuratedBadgeDefinitionEventMessageSupplierLocalListIT(
     @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorRelayUrl, superconductorInstanceIdentity);
  }

  @Override
  protected List<BadgeDefinitionGenericEvent> createBadgeDefinitionGenericEventList() {
    return List.of(
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          definitionEventRelay),
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag)
    );
  }
}
