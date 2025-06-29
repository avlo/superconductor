package com.prosilion.superconductor.util;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardUpvoteEvent extends AbstractBadgeAwardEvent<KindTypeIF> {
  private final static String UPVOTE_CONTENT = "1";

  public BadgeAwardUpvoteEvent(
      @NonNull Identity identity,
      @NonNull PublicKey upvotedUser) throws NostrException, NoSuchAlgorithmException {
    super(TestKindType.UPVOTE, identity,
        new Vote(
            identity.getPublicKey(),
            upvotedUser,
            TestKindType.UPVOTE).getAwardEvent(),
        UPVOTE_CONTENT);
  }

  public BadgeAwardUpvoteEvent(
      @NonNull Identity identity,
      @NonNull PublicKey upvotedUser,
      @NonNull List<BaseTag> tags) throws NostrException, NoSuchAlgorithmException {
    super(TestKindType.UPVOTE, identity,
        new Vote(
            identity.getPublicKey(),
            upvotedUser,
            TestKindType.UPVOTE).getAwardEvent(),
        tags,
        UPVOTE_CONTENT);
  }
}
