package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;

public interface GenericTagDto {
  String getCode();
  <T extends GenericTagEntity> T convertDtoToEntity();
}
