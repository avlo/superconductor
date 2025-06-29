package com.prosilion.superconductor.util;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.internal.AwardEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
@EqualsAndHashCode(callSuper = false)
public class Vote {
  private final AwardEvent awardEvent;

  public Vote(@NonNull PublicKey voter, @NonNull PublicKey upvotedUser, @NonNull KindTypeIF voteKindType) {
    IdentifierTag identifierTag = new IdentifierTag(voteKindType.getName());
    AddressTag addressTag = new AddressTag(
        Kind.BADGE_DEFINITION_EVENT,
        voter,
        identifierTag);

    awardEvent = new AwardEvent(addressTag, new PubKeyTag(upvotedUser));
  }
}

