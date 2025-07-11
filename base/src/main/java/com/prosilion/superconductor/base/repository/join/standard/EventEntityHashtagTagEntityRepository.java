package com.prosilion.superconductor.base.repository.join.standard;

import com.prosilion.superconductor.base.entity.join.standard.EventEntityHashtagTagEntity;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityHashtagTagEntityRepository<T extends EventEntityHashtagTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "t";
  }
}
