package com.prosilion.superconductor.base.repository.join.standard;

import com.prosilion.superconductor.base.entity.join.standard.EventEntityReferenceTagEntity;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityReferenceTagEntityRepository<T extends EventEntityReferenceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "r";
  }
}
