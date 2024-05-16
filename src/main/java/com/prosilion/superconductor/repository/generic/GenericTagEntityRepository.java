package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;

public interface GenericTagEntityRepository<T extends GenericTagEntity> {
  String getCode();
  Long save(T genericTagEntity);
}