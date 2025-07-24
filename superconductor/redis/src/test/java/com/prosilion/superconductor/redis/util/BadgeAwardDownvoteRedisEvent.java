package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.type.SuperconductorKindType;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardDownvoteRedisEvent extends AbstractBadgeAwardEvent<KindTypeIF> {
  public BadgeAwardDownvoteRedisEvent(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent) throws NostrException, NoSuchAlgorithmException {
    super(SuperconductorKindType.DOWNVOTE, identity,
        new Vote(downvotedUser, downvoteBadgeDefinitionEvent).getAwardEvent(),
        downvoteBadgeDefinitionEvent.getContent());
  }

  public BadgeAwardDownvoteRedisEvent(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent,
      @NonNull List<BaseTag> tags) throws NostrException, NoSuchAlgorithmException {
    super(SuperconductorKindType.DOWNVOTE, identity,
        new Vote(
            downvotedUser,
            downvoteBadgeDefinitionEvent).getAwardEvent(),
        tags,
        downvoteBadgeDefinitionEvent.getContent());
  }
}
