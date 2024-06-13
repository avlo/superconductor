package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityHashtagTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityHashtagTagEntityRepository<T extends EventEntityHashtagTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "t";
  }
}