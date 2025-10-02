package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityIdentifierTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityIdentifierTagJpaEntityRepository<T extends EventEntityIdentifierTagJpaEntity> extends EventEntityAbstractTagJpaEntityRepository<T> {
  default String getCode() {
    return "d";
  }
}
