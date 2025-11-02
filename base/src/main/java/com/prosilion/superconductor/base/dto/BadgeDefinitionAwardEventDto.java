package com.prosilion.superconductor.base.dto;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import java.util.Objects;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class BadgeDefinitionAwardEventDto {
  private final BadgeDefinitionAwardEvent badgeDefinitionAwardEvent;

  public BadgeDefinitionAwardEventDto(@NonNull BadgeDefinitionAwardEvent badgeDefinitionAwardEvent) throws NostrException {
    this.badgeDefinitionAwardEvent = badgeDefinitionAwardEvent;
  }

  public IdentifierTag getIdentifierTag() {
    return badgeDefinitionAwardEvent.getIdentifierTag();
  }

  @Override
  public boolean equals(Object obj) {
    if (!obj.getClass().getSuperclass().isAssignableFrom(BadgeDefinitionAwardEventDto.class))
      return false;

    BadgeDefinitionAwardEventDto that = (BadgeDefinitionAwardEventDto) obj;
    return
        this.badgeDefinitionAwardEvent.equals(that.badgeDefinitionAwardEvent);
  }
}
