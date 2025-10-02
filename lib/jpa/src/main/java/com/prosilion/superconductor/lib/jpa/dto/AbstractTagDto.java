package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;

public interface AbstractTagDto {
  String getCode();
  AbstractTagJpaEntity convertDtoToJpaEntity();
}
