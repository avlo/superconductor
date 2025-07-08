package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityReferenceTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityReferenceTagEntityRepository<T extends EventEntityReferenceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "r";
  }
}
