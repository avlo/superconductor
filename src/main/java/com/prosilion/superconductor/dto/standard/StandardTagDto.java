package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.AbstractTagEntity;

public interface StandardTagDto {
  String getCode();
  AbstractTagEntity convertDtoToEntity();
}
