package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;

public interface AbstractTagDto {
  String getCode();

  AbstractTagEntity convertDtoToEntity();
}
