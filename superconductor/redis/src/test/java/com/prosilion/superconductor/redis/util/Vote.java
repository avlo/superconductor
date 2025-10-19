package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
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

  public Vote(@NonNull PublicKey upvotedUser, @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent) {
    AddressTag addressTag = new AddressTag(
        Kind.BADGE_DEFINITION_EVENT,
        badgeDefinitionUpvoteEvent.getReferencePubkeyTag().getPublicKey(),
        badgeDefinitionUpvoteEvent.getIdentifierTag());

    awardEvent = new AwardEvent(addressTag, new PubKeyTag(upvotedUser));
  }
}

