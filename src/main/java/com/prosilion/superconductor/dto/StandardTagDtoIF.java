package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.standard.StandardTagEntity;

public interface StandardTagDtoIF {
  Character getCode();
  <T extends StandardTagEntity> T convertDtoToEntity();
}
