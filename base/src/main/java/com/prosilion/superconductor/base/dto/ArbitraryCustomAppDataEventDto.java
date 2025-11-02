package com.prosilion.superconductor.base.dto;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.tag.IdentifierTag;
import java.util.Objects;
import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public class ArbitraryCustomAppDataEventDto {
  private final IdentifierTag identifierTag;
  private final String content;

  public ArbitraryCustomAppDataEventDto(@NonNull IdentifierTag identifierTag, @NonNull String content) throws NostrException {
    this.identifierTag = identifierTag;
    this.content = content;
  }

  @Override
  public boolean equals(Object obj) {
    if (!obj.getClass().isAssignableFrom(ArbitraryCustomAppDataEventDto.class) && !obj.getClass().getSuperclass().isAssignableFrom(ArbitraryCustomAppDataEventDto.class)) 
      return false;

    ArbitraryCustomAppDataEventDto that = (ArbitraryCustomAppDataEventDto) obj;
    return
        Objects.equals(
            this.identifierTag,
            that.identifierTag) 
          &&
        Objects.equals(
            this.content,
            that.content);
  }
}
