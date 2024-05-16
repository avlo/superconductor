package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import lombok.Data;
import nostr.event.tag.GeohashTag;
import org.springframework.beans.BeanUtils;

@Data
public class GeohashTagDto extends GeohashTag implements GenericTagDto {

  public GeohashTagDto(String location) {
    super(location);
  }

  @Override
  public <T extends GenericTagEntity> T convertDtoToEntity() {
    T geohashTagEntity = (T) new GeohashTagEntity();
    BeanUtils.copyProperties(this, geohashTagEntity, "code");
    geohashTagEntity.setCode("g");
    return geohashTagEntity;
  }
}
