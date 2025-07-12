package prosilion.superconductor.lib.jpa.dto;

import prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;

public interface AbstractTagDto {
  String getCode();

  AbstractTagEntity convertDtoToEntity();
}
