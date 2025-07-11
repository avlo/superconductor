package com.prosilion.superconductor.base.dto.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.entity.standard.HashtagTagEntity;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.HashtagTag;

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
