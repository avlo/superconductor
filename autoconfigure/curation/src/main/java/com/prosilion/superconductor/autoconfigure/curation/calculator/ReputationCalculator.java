package com.prosilion.superconductor.autoconfigure.curation.calculator;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;

public class ReputationCalculator implements ReputationCalculatorIF {
  private final Identity instanceIdentity;
  private final String relayUrl;

  public ReputationCalculator(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl) {
    this.instanceIdentity = superconductorInstanceIdentity;
    this.relayUrl = superconductorRelayUrl;
  }

  public BadgeAwardReputationEvent calculateUpdatedReputationEvent(
     @NonNull PublicKey voteReceiverPubkey,
     @NonNull BadgeAwardReputationEvent previousReputationEvent,
     @NonNull List<CuratedFormulaEvent> curatedFormulaEventList,
     @NonNull List<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEventList) throws NostrException {
    if (curatedFormulaEventList.isEmpty())
      throw new NostrException("calculateUpdatedReputationEvent received empty List<CuratedFormulaEvent>");
    return createReputationEvent(
       voteReceiverPubkey,
       calculateReputationEventScore(
          curatedFormulaEventList.stream()
             .filter(curatedFormulaEvent ->
                contains(curatedBadgeAwardGenericEventList, curatedFormulaEvent)),
          previousReputationEvent),
       previousReputationEvent.getBadgeDefinitionEvent());
  }

  private boolean contains(@NonNull List<CuratedBadgeAwardGenericEvent> curatedBadgeAwardGenericEventList, CuratedFormulaEvent curatedFormulaEvent) {
    return curatedBadgeAwardGenericEventList.stream()
       .map(CuratedBadgeAwardGenericEvent::getAddressTag)
       .map(AddressTag::getIdentifierTag).toList().contains(curatedFormulaEvent.getIdentifierTag());
  }

  private String calculateReputationEventScore(Stream<CuratedFormulaEvent> formulaEvents, BadgeAwardReputationEvent previousReputationEvent) {
    return formulaEvents.map(CuratedFormulaEvent::getFormula).reduce(previousReputationEvent.getScore(), ExpressionCalculator::calculate);
  }

  private BadgeAwardReputationEvent createReputationEvent(
     @NonNull PublicKey badgeReceiverPubkey,
     @NonNull String score,
     @NonNull BadgeDefinitionReputationEvent badgeDefinitionReputationEvent) throws NostrException {
    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent(
       instanceIdentity,
       badgeReceiverPubkey,
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEvent,
       new BigDecimal(score),
       new Relay(relayUrl));
    return badgeAwardReputationEvent;
  }

  @Override
  public String getFullyQualifiedCalculatorName() {
    return getClass().getName();
  }
}
