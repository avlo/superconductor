package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityReferenceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityReferenceTagJpaEntityRepository<T extends EventEntityReferenceTagJpaEntity> extends EventEntityAbstractTagJpaEntityRepository<T> {
  default String getCode() {
    return "r";
  }
}
