package com.prosilion.superconductor.lib.jpa.dto.generic;

import java.util.List;
import com.prosilion.superconductor.lib.jpa.entity.generic.GenericTagJpaEntity;

public record GenericTagDto(String code, List<ElementAttributeDto> atts) {
  public GenericTagJpaEntity convertDtoToEntity() {
    return new GenericTagJpaEntity(code);
  }
}
