package com.prosilion.superconductor.h2db.util;

import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardDownvoteEvent extends AbstractBadgeAwardEvent {
  public BadgeAwardDownvoteEvent(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent) {
    this(identity, downvotedUser, downvoteBadgeDefinitionEvent, List.of());
  }

  public BadgeAwardDownvoteEvent(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent,
      @NonNull List<BaseTag> tags) {
    super(identity,
        new Vote(
            downvotedUser,
            downvoteBadgeDefinitionEvent).getAwardEvent(),
        tags,
        downvoteBadgeDefinitionEvent.getContent());
  }
}
