package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardUpvoteRedisEvent extends AbstractBadgeAwardEvent {
  public BadgeAwardUpvoteRedisEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeDefinitionEvent upvoteBadgeDefinitionEvent) {
    this(authorIdentity, upvotedUser, upvoteBadgeDefinitionEvent, List.of());
  }

  public BadgeAwardUpvoteRedisEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
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
