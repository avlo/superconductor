package prosilion.superconductor.lib.jpa.dto.generic;

import java.util.List;
import prosilion.superconductor.lib.jpa.entity.generic.GenericTagEntity;

public record GenericTagDto(String code, List<ElementAttributeDto> atts) {
  public GenericTagEntity convertDtoToEntity() {
    return new GenericTagEntity(code);
  }
}
