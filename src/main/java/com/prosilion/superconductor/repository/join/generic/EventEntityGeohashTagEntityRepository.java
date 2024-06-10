package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityGeohashTagEntityRepository<T extends EventEntityGeohashTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "g";
  }
}