package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.standard.HashtagTagEntity;
import lombok.NonNull;
import nostr.event.tag.HashtagTag;

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
  public HashtagTagEntity convertDtoToEntity() {
    return new HashtagTagEntity(hashtagTag);
  }
}
