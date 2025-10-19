package com.prosilion.superconductor.h2db.util;

import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardUpvoteEvent extends BadgeAwardAbstractEvent {
  public BadgeAwardUpvoteEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent) {
    this(authorIdentity, upvotedUser, badgeDefinitionUpvoteEvent, List.of());
  }

  public BadgeAwardUpvoteEvent(
      @NonNull Identity authorIdentity,
      @NonNull PublicKey upvotedUser,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull List<BaseTag> tags) {
    super(
        authorIdentity,
        new Vote(
            upvotedUser,
            badgeDefinitionUpvoteEvent).getAwardEvent(),
        tags,
        badgeDefinitionUpvoteEvent.getContent());
  }
}
