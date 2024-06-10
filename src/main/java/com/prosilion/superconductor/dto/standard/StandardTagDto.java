package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.StandardTagEntity;

public interface StandardTagDto {
  String getCode();
  StandardTagEntity convertDtoToEntity();
}
