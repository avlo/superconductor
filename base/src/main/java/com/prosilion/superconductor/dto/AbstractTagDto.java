package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.AbstractTagEntity;

public interface AbstractTagDto {
  String getCode();

  AbstractTagEntity convertDtoToEntity();
}
