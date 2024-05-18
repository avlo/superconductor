package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.generic.HashtagTagEntity;
import lombok.Getter;
import nostr.event.tag.HashtagTag;

@Getter
public class HashtagTagDto extends GenericTagDto implements GenericTagDtoIF {
  private final HashtagTag hashtagTag;
  public HashtagTagDto(String hashTag) {
    super(hashTag);
    hashtagTag = HashtagTag.builder().hashTag(hashTag).build();
  }

  @Override
  public Character getCode() {
    return hashtagTag.getCode().charAt(0);
  }
  @Override
  public <T extends GenericTagEntity> T convertDtoToEntity() {
    return (T) convertDtoToEntity(new HashtagTagEntity(getCode(), getValue()), getCode());
  }
}
