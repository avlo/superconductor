package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.AbstractTagEntity;

// TODO: refactor, may be unnecessary
public interface StandardTagDto {
  String getCode();
  AbstractTagEntity convertDtoToEntity();
}
