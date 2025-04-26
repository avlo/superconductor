package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.standard.GeohashTagEntity;
import lombok.NonNull;
import nostr.event.tag.GeohashTag;

public class GeohashTagDto implements AbstractTagDto {
  private final GeohashTag geohashTag;

  public GeohashTagDto(@NonNull GeohashTag geohashTag) {
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
