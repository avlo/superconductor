package com.prosilion.superconductor.base.repository.join.standard;

import com.prosilion.superconductor.base.entity.join.standard.EventEntityEventTagEntity;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityEventTagEntityRepository<T extends EventEntityEventTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "e";
  }
}
