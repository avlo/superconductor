package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
public abstract class GenericTagDto implements GenericTagDtoIF {
  private final String value;
  GenericTagDto(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  protected <T extends GenericTagEntity> T convertDtoToEntity(T genericTagEntity, String code) {
    BeanUtils.copyProperties(this, genericTagEntity, code);
    genericTagEntity.setCode(code);
    return genericTagEntity;
  }
}
