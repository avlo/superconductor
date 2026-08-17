package java.com.prosilion.superconductor.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.com.prosilion.superconductor.supplier.AbstractBaseCacheCuratedFormulaEventMessageIT;
import java.util.List;
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
  protected List<FormulaEvent> createFormulaEventList() {
    return List.of(
       createFormulaEventContainingRelayTag()
       , createFormulaEventWithoutRelayTag()
    );
  }

  protected FormulaEvent createFormulaEventContainingRelayTag() {
    return new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       new BadgeDefinitionGenericEvent(
          upvoteDefnCreator,
          upvoteIdentifierTag,
          formulaEventRelay),
       PLUS_ONE_FORMULA,
       formulaEventRelay);
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
  public void overridableValidateCorrectlyCreatedAndPersistedFormulaEventVariant(List<FormulaEvent> formulaEvents) {
    validateCorrectlyCreatedAndPersistedCuratedFormulaEventVariants(formulaEvents);
  }

  @Override
  public void overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(badgeDefinitionGenericEvent);
  }
}
