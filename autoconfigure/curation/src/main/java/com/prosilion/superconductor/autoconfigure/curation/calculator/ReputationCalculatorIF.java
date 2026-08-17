package com.prosilion.superconductor.autoconfigure.curation.calculator;

import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;

public interface ReputationCalculatorIF {
  BadgeAwardReputationEvent calculateUpdatedReputationEvent(
     PublicKey voteReceiverPubkey,
     BadgeAwardReputationEvent previousReputationEvent,
     List<CuratedFormulaEvent> formulaEvents,
     FollowSetsEvent incomingFollowSetsEvent);

  String getFullyQualifiedCalculatorName();
}
