package com.prosilion.superconductor.base.dto;

import com.prosilion.superconductor.base.entity.AbstractTagEntity;

public interface AbstractTagDto {
  String getCode();

  AbstractTagEntity convertDtoToEntity();
}
