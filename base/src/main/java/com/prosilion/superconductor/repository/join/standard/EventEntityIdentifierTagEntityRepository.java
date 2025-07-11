package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityIdentifierTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityIdentifierTagEntityRepository<T extends EventEntityIdentifierTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "d";
  }
}