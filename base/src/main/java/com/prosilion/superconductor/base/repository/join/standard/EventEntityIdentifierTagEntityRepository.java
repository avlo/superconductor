package com.prosilion.superconductor.base.repository.join.standard;

import com.prosilion.superconductor.base.entity.join.standard.EventEntityIdentifierTagEntity;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityIdentifierTagEntityRepository<T extends EventEntityIdentifierTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "d";
  }
}
