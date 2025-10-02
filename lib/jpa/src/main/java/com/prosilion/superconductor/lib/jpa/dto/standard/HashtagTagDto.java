package com.prosilion.superconductor.lib.jpa.dto.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.superconductor.lib.jpa.entity.standard.HashtagTagJpaEntity;

public class HashtagTagDto implements AbstractTagDto {
  private final HashtagTag hashtagTag;

  public HashtagTagDto(@NonNull HashtagTag hashtagTag) {
    this.hashtagTag = hashtagTag;
  }

  @Override
  public String getCode() {
    return hashtagTag.getCode();
  }

  @Override
  public HashtagTagJpaEntity convertDtoToJpaEntity() {
    return new HashtagTagJpaEntity(hashtagTag);
  }
}
