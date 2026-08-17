package com.prosilion.superconductor.autoconfigure.curation.service.reputation;

import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.curation.calculator.ReputationCalculatorIF;
import com.prosilion.superconductor.autoconfigure.curation.service.ReputationCalculationServiceIF;
import java.util.List;
import lombok.NonNull;

public class ReputationCalculationLocalService implements ReputationCalculationServiceIF {
  ReputationCalculatorIF reputationCalculator;

  public ReputationCalculationLocalService(@NonNull ReputationCalculatorIF reputationCalculator) {
    this.reputationCalculator = reputationCalculator;
  }

  @Override
  public BadgeAwardReputationEvent calculateReputationEvent(
     @NonNull PublicKey voteReceiverPubkey,
     @NonNull BadgeAwardReputationEvent previousReputationEvent,
     @NonNull List<CuratedFormulaEvent> formulaEvents,
     @NonNull FollowSetsEvent incomingFollowSetsEvent) {
    return reputationCalculator.calculateUpdatedReputationEvent(voteReceiverPubkey, previousReputationEvent, formulaEvents, incomingFollowSetsEvent);
  }
}
