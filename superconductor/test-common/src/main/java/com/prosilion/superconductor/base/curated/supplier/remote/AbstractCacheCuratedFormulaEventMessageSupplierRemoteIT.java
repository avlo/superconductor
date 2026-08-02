package com.prosilion.superconductor.base.curated.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedFormulaEventMessageIT;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedFormulaEventMessageSupplierRemoteIT extends AbstractBaseCacheCuratedFormulaEventMessageIT {
  protected AbstractCacheCuratedFormulaEventMessageSupplierRemoteIT(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl) throws NostrException {
    super(cacheServiceIF, definitionEventRelayUrl, superconductorInstanceIdentity);
  }

  @Override
  protected BadgeDefinitionGenericEvent createDefinitionEventContainingRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       new Relay("ws://superconductor-app-two:5555"));
  }

  @Override
  protected BadgeDefinitionGenericEvent createDefinitionEventWithoutRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);
  }

  @Override
  protected FormulaEvent createFormulaEventContainingRelayTag() {
    return new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       badgeDefinitionUpvoteEventWithRelayTag,
       PLUS_ONE_FORMULA,
       new Relay("ws://superconductor-app-two:5555"));
  }

  @Override
  protected FormulaEvent createFormulaEventWithoutRelayTag() {
    return new FormulaEvent(
       formulaCreator,
       formulaDownvoteIdentifierTag,
       badgeDefinitionDownvoteEventWithoutRelayTag,
       MINUS_ONE_FORMULA);
  }
}
