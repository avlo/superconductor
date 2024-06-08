package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;

public interface GenericTagDtoIF {
  Character getCode();
  String getValue();
  <T extends GenericTagEntity> T convertDtoToEntity();
}
