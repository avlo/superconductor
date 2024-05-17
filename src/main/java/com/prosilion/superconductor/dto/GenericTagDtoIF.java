package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;

public interface GenericTagDtoIF {
  Character getCode();
  String getValue();
  <T extends GenericTagEntity> T convertDtoToEntity();
}
