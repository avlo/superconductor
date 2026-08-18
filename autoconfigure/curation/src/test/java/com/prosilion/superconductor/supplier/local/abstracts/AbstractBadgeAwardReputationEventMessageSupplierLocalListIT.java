package com.prosilion.superconductor.supplier.local.abstracts;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.supplier.AbstractBaseBadgeAwardReputationEventMessageListIT;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBadgeAwardReputationEventMessageSupplierLocalListIT extends AbstractBaseBadgeAwardReputationEventMessageListIT {

  protected AbstractBadgeAwardReputationEventMessageSupplierLocalListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) {
    super(superconductorInstanceIdentity, definitionEventRelayUrl, awardEventRelayUrl);
  }

  @Override
  protected List<FormulaEvent> createFormulaEventList() {
    return List.of(
       createPlusOneFormulaEvent()
//       ,
//       createMinusOneFormulaEvent()
    );
  }

  @Override
  protected List<BadgeDefinitionGenericEvent> createBadgeDefinitionGenericEventList() {
    return List.of(
       createBadgeAwardUpvoteDefinitionEvent()
//       ,
//       createBadgeAwardDownvoteDefinitionEvent()
    );
  }

  protected BadgeDefinitionGenericEvent createBadgeAwardUpvoteDefinitionEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       String.format("awardUpvoteDefinitionEvent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       new Relay(definitionEventRelayUrl));
  }

  protected BadgeDefinitionGenericEvent createBadgeAwardDownvoteDefinitionEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag,
       String.format("awardDownvoteDefinitionEvent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       new Relay(definitionEventRelayUrl));
  }

  protected FormulaEvent createPlusOneFormulaEvent() {
    return new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       badgeDefinitionGenericEventList.getFirst(),
       PLUS_ONE_FORMULA,
       new Relay(definitionEventRelayUrl));
  }

  protected FormulaEvent createMinusOneFormulaEvent() {
    return new FormulaEvent(
       formulaCreator,
       formulaDownvoteIdentifierTag,
       badgeDefinitionGenericEventList.get(1),
       MINUS_ONE_FORMULA,
       new Relay(definitionEventRelayUrl));
  }
}
