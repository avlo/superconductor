package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityGeohashTagEntityRepository<T extends EventEntityGeohashTagEntity> extends EventEntityGenericTagEntityRepository<T> {
  default Character getCode() {
    return 'g';
  }
}