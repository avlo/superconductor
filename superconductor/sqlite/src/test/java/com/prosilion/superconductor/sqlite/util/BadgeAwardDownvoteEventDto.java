package com.prosilion.superconductor.sqlite.util;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.dto.BadgeAwardAbstractEventDto;
import java.util.List;
import org.springframework.lang.NonNull;

public class BadgeAwardDownvoteEventDto extends BadgeAwardAbstractEventDto {
  public BadgeAwardDownvoteEventDto(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
    this(identity, downvotedUser, badgeDefinitionDownvoteEvent, List.of());
  }

  public BadgeAwardDownvoteEventDto(
      @NonNull Identity identity,
      @NonNull PublicKey downvotedUser,
      @NonNull BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent,
      @NonNull List<BaseTag> tags) {
    super(identity,
        new Vote(
            downvotedUser,
            badgeDefinitionDownvoteEvent).getAwardEvent(),
        tags,
        badgeDefinitionDownvoteEvent.getContent());
  }
}
