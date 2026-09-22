package com.prosilion.superconductor.autoconfigure.curation.calculator;

import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;

public interface ReputationCalculatorIF {
  default BadgeAwardReputationEvent calculateUpdatedReputationEvent(
     PublicKey voteReceiverPubkey,
     BadgeAwardReputationEvent previousReputationEvent,
     List<CuratedFormulaEvent> formulaEvents,
     CuratedBadgeAwardCanonicalEvent curatedBadgeAwardCanonicalEventList) {
    return calculateUpdatedReputationEvent(voteReceiverPubkey, previousReputationEvent, formulaEvents,
       List.of(curatedBadgeAwardCanonicalEventList));
  }

  BadgeAwardReputationEvent calculateUpdatedReputationEvent(
     PublicKey voteReceiverPubkey,
     BadgeAwardReputationEvent previousReputationEvent,
     List<CuratedFormulaEvent> formulaEvents,
     List<CuratedBadgeAwardCanonicalEvent> curatedBadgeAwardCanonicalEventList);

  String getFullyQualifiedCalculatorName();
}
