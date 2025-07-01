package com.prosilion.superconductor.util;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardDownvoteEvent extends AbstractBadgeAwardEvent<KindTypeIF> {
  public BadgeAwardDownvoteEvent(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent) throws NostrException, NoSuchAlgorithmException {
    super(TestKindType.DOWNVOTE, identity,
        new Vote(downvotedUser, downvoteBadgeDefinitionEvent).getAwardEvent(),
        downvoteBadgeDefinitionEvent.getContent());
  }

  public BadgeAwardDownvoteEvent(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent,
      @NonNull List<BaseTag> tags) throws NostrException, NoSuchAlgorithmException {
    super(TestKindType.DOWNVOTE, identity,
        new Vote(
            downvotedUser,
            downvoteBadgeDefinitionEvent).getAwardEvent(),
        tags,
        downvoteBadgeDefinitionEvent.getContent());
  }
}
