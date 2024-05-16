package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;

public interface EventEntityGenericTagEntityRepository<T extends EventEntityGenericTagEntity> {
  String getKey();
  Long save(T joinEntity);
}