package com.prosilion.superconductor.base.repository.join.standard;

import com.prosilion.superconductor.base.entity.join.standard.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityGeohashTagEntityRepository<T extends EventEntityGeohashTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "g";
  }
}
