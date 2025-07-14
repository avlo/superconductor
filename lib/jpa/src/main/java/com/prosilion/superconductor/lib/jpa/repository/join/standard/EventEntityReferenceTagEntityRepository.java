package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityReferenceTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityReferenceTagEntityRepository<T extends EventEntityReferenceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "r";
  }
}
