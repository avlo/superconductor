package com.prosilion.superconductor.base.dto.generic;

import com.prosilion.superconductor.base.entity.generic.GenericTagEntity;

import java.util.List;

public record GenericTagDto(String code, List<ElementAttributeDto> atts) {
  public GenericTagEntity convertDtoToEntity() {
    return new GenericTagEntity(code);
  }
}
