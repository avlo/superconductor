package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import lombok.Getter;
import nostr.event.tag.GeohashTag;

@Getter
public class GeohashTagDto extends GenericTagDto implements GenericTagDtoIF {
  private final GeohashTag geohashTag;

  public GeohashTagDto(String location) {
    super(location);
    geohashTag = GeohashTag.builder().location(location).build();
  }

  @Override
  public Character getCode() {
    return 'g';
  }
  @Override
  public <T extends GenericTagEntity> T convertDtoToEntity() {
    return (T) new GeohashTagEntity(getCode(), getValue());
  }
}