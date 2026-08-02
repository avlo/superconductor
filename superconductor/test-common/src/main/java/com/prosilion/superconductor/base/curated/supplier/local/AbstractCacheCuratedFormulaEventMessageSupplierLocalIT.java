package com.prosilion.superconductor.base.curated.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.curated.supplier.AbstractBaseCacheCuratedFormulaEventMessageIT;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedFormulaEventMessageSupplierLocalIT extends AbstractBaseCacheCuratedFormulaEventMessageIT {
  protected AbstractCacheCuratedFormulaEventMessageSupplierLocalIT(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(cacheServiceIF, superconductorRelayUrl, superconductorInstanceIdentity);
  }

  @Override
  protected BadgeDefinitionGenericEvent createDefinitionEventContainingRelayTag() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       formulaEventRelay);
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
       formulaEventRelay);
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
