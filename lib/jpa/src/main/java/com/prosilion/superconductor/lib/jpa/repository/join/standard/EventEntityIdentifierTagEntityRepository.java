package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityIdentifierTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityIdentifierTagEntityRepository<T extends EventEntityIdentifierTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "d";
  }
}
