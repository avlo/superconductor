package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;

public interface GenericTagDtoIF {
  String getCode();
  String getValue();
  <T extends GenericTagEntity> T convertDtoToEntity();
}
