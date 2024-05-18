package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends GenericTagEntityRepository<T> {
  default Character getCode() {
    return 'g';
  }
}