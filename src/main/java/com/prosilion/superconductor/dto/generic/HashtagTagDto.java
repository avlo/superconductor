package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.entity.generic.HashtagTagEntity;
import nostr.event.tag.HashtagTag;

public class HashtagTagDto implements StandardTagDto {
  private final HashtagTag hashtagTag;
  public HashtagTagDto(HashtagTag hashtagTag) {
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
