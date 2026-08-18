package com.prosilion.superconductor.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.supplier.AbstractBaseCacheCuratedFormulaEventMessageIT;
import java.util.List;
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
  protected List<FormulaEvent> createFormulaEventList() {
    return List.of(
       createFormulaEventContainingRelayTag()
//       , createFormulaEventWithoutRelayTag()
    );
  }

  protected FormulaEvent createFormulaEventContainingRelayTag() {
    return new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          new Relay("ws://superconductor-app-three:5555")),
       PLUS_ONE_FORMULA,
       new Relay("ws://superconductor-app-three:5555"));
  }

  protected FormulaEvent createFormulaEventWithoutRelayTag() {
    return new FormulaEvent(
       formulaCreator,
       formulaDownvoteIdentifierTag,
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          downvoteIdentifierTag),
       MINUS_ONE_FORMULA);
  }

  @Override
  public void overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(badgeDefinitionGenericEvent);
  }

  @Override
  public void overridableValidateCorrectlyCreatedAndPersistedFormulaEventVariant(List<FormulaEvent> formulaEventList) {
    validateCorrectlyCreatedAndPersistedCuratedFormulaEventVariants(formulaEventList);
  }
}
