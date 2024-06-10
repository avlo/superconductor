package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityHashtagTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityHashtagTagEntityRepository<T extends EventEntityHashtagTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "t";
  }
}