package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;

public interface GenericTagDto {
  void setCode(String code);
  String getCode();
  <T extends GenericTagEntity> T convertDtoToEntity();
}
