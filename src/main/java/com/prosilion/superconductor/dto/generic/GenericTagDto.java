package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.dto.standard.ElementAttributeDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;

import java.util.List;

public record GenericTagDto(String code, List<ElementAttributeDto> atts) {
  public GenericTagEntity convertDtoToEntity() {
    return new GenericTagEntity(code);
  }
}
