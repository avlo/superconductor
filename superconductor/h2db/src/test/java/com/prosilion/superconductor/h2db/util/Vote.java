package com.prosilion.superconductor.h2db.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardDefinitionEvent;
import com.prosilion.nostr.event.internal.AwardEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
@EqualsAndHashCode(callSuper = false)
public class Vote {
  private final AwardEvent awardEvent;

  public Vote(@NonNull PublicKey upvotedUser, @NonNull BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent) {
    AddressTag addressTag = new AddressTag(
        Kind.BADGE_DEFINITION_EVENT,
        upvoteBadgeDefinitionEvent.getReferencePubkeyTag().getPublicKey(),
        upvoteBadgeDefinitionEvent.getIdentifierTag());

    awardEvent = new AwardEvent(addressTag, new PubKeyTag(upvotedUser));
  }
}

