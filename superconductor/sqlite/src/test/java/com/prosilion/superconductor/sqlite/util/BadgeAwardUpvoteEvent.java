package com.prosilion.superconductor.sqlite.util;

import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.event.BadgeAwardDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardUpvoteEvent extends AbstractBadgeAwardEvent {
  public BadgeAwardUpvoteEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent) {
    this(authorIdentity, upvotedUser, upvoteBadgeDefinitionEvent, List.of());
  }

  public BadgeAwardUpvoteEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull List<BaseTag> tags) {
    super(
        authorIdentity,
        new Vote(
            upvotedUser,
            upvoteBadgeDefinitionEvent).getAwardEvent(),
        tags,
        upvoteBadgeDefinitionEvent.getContent());
  }
}
