package com.prosilion.superconductor.autoconfigure.curation.calculator;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;

public class DynamicReputationCalculator implements ReputationCalculatorIF {
  private final Identity aImgIdentity;
  private final String afterimageRelayUrl;

  public DynamicReputationCalculator(
     @NonNull String afterimageRelayUrl,
     @NonNull Identity aImgIdentity) {
    this.aImgIdentity = aImgIdentity;
    this.afterimageRelayUrl = afterimageRelayUrl;
  }

  public BadgeAwardReputationEvent calculateUpdatedReputationEvent(
     @NonNull PublicKey voteReceiverPubkey,
     @NonNull BadgeAwardReputationEvent previousReputationEvent,
     @NonNull List<CuratedFormulaEvent> curatedFormulaEventList,
     @NonNull FollowSetsEvent incomingFollowSetsEvent) throws NostrException {
    if (curatedFormulaEventList.isEmpty())
      throw new NostrException("calculateUpdatedReputationEvent received empty List<CuratedFormulaEvent>");

    return createReputationEvent(
       voteReceiverPubkey,
       calculateReputationEventScore(
          curatedFormulaEventList.stream()
             .filter(curatedFormulaEvent ->
                incomingFollowSetsEvent.getBadgeSetsEventList().stream()
                   .map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList)
                   .flatMap(Collection::stream)
                   .map(CuratedBadgeAwardGenericEvent::getAddressTag)
                   .map(AddressTag::getIdentifierTag).toList().contains(
                      curatedFormulaEvent.getIdentifierTag())),
          previousReputationEvent),
       previousReputationEvent.getBadgeDefinitionEvent());
  }

  private String calculateReputationEventScore(Stream<CuratedFormulaEvent> formulaEvents, BadgeAwardReputationEvent previousReputationEvent) {
    return formulaEvents
       .map(CuratedFormulaEvent::getFormula)
       .reduce(previousReputationEvent.getScore(), ExpressionCalculator::calculate);
  }

  private BadgeAwardReputationEvent createReputationEvent(
     @NonNull PublicKey badgeReceiverPubkey,
     @NonNull String score,
     @NonNull BadgeDefinitionReputationEvent badgeDefinitionReputationEvent) throws NostrException {
    return new BadgeAwardReputationEvent(
       aImgIdentity,
       badgeReceiverPubkey,
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEvent,
       new BigDecimal(score),
       new Relay(afterimageRelayUrl));
  }

  @Override
  public String getFullyQualifiedCalculatorName() {
    return getClass().getName();
  }
}
