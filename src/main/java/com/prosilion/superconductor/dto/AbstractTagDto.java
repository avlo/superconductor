package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.AbstractTagEntity;

// TODO: refactor, may be unnecessary
public interface AbstractTagDto {
  String getCode();
  AbstractTagEntity convertDtoToEntity();
}
