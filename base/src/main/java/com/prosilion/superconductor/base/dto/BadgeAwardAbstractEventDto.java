package com.prosilion.superconductor.base.dto;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.internal.AwardEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.Identity;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public abstract class BadgeAwardAbstractEventDto extends BaseEvent {
  public BadgeAwardAbstractEventDto(
      @NonNull Identity identity,
      @NonNull AwardEvent awardEvent,
      @NonNull String content) throws NostrException {
    this(identity, awardEvent, List.of(), content);
  }

  public BadgeAwardAbstractEventDto(
      @NonNull Identity identity,
      @NonNull AwardEvent awardEvent,
      @NonNull List<BaseTag> tags,
      @NonNull String content) throws NostrException {
    super(identity, Kind.BADGE_AWARD_EVENT,
        Stream.concat(
                Stream.concat(
                    Stream.of(awardEvent.addressTag()),
                    awardEvent.pubkeyTags().stream()),
                tags.stream()).toList(),
        content);
  }
}
