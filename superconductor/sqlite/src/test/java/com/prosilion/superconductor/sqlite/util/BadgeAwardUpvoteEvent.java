package com.prosilion.superconductor.sqlite.util;

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

public class BadgeAwardUpvoteEvent extends AbstractBadgeAwardEvent<KindTypeIF> {
  public BadgeAwardUpvoteEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeDefinitionEvent upvoteBadgeDefinitionEvent) throws NostrException, NoSuchAlgorithmException {
    super(
        SuperconductorKindType.UNIT_UPVOTE,
        authorIdentity,
        new Vote(
            upvotedUser,
            upvoteBadgeDefinitionEvent).getAwardEvent(),
        upvoteBadgeDefinitionEvent.getContent());
  }

  public BadgeAwardUpvoteEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull List<BaseTag> tags) throws NostrException, NoSuchAlgorithmException {
    super(
        SuperconductorKindType.UNIT_UPVOTE,
        authorIdentity,
        new Vote(
            upvotedUser,
            upvoteBadgeDefinitionEvent).getAwardEvent(),
        tags,
        upvoteBadgeDefinitionEvent.getContent());
  }
}
