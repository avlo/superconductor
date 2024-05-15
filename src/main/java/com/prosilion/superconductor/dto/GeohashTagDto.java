package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.GenericTagEntity;
import com.prosilion.superconductor.entity.GeohashTagEntity;
import nostr.event.tag.GeohashTag;
import org.springframework.beans.BeanUtils;

public class GeohashTagDto extends GeohashTag implements GenericTagDto {

  public GeohashTagDto(String location) {
    super(location);
  }

  @Override
  public void setCode(String key) {
    super.setLocation(key);
  }

  @Override
  public String getCode() {
    return super.getLocation();
  }

  @Override
  public GenericTagEntity convertDtoToEntity() {
    GenericTagEntity geohashTagEntity = new GeohashTagEntity();
    BeanUtils.copyProperties(this, geohashTagEntity);
    return geohashTagEntity;
  }
}
