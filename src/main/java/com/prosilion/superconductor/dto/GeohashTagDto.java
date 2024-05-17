package com.prosilion.superconductor.dto;

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
  public String getCode() {
    return getGeohashTag().getCode();
  }
  @Override
  public <T extends GenericTagEntity> T convertDtoToEntity() {
    return (T) convertDtoToEntity(new GeohashTagEntity(getCode(), getValue()), getCode());
  }
}
