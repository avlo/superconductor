package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GeohashTagEntity;
import com.prosilion.superconductor.repository.standard.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeohashTagEntityRepository<T extends GeohashTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "g";
  }
}