package com.prosilion.superconductor.lib.jpa.dto.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.superconductor.lib.jpa.entity.standard.GeohashTagJpaEntity;

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
  public GeohashTagJpaEntity convertDtoToJpaEntity() {
    return new GeohashTagJpaEntity(geohashTag);
  }
}
