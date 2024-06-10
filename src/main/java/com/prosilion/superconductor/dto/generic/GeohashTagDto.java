package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import nostr.event.tag.GeohashTag;

public class GeohashTagDto implements StandardTagDto {
  private final GeohashTag geohashTag;

  public GeohashTagDto(GeohashTag geohashTag) {
    this.geohashTag = geohashTag;
  }

  @Override
  public String getCode() {
    return geohashTag.getCode();
  }

  @Override
  public GeohashTagEntity convertDtoToEntity() {
    return new GeohashTagEntity(geohashTag);
  }
}
