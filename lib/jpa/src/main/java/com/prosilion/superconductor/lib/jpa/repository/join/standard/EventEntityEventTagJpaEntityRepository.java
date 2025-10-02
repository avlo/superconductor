package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityEventTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityEventTagJpaEntityRepository<T extends EventEntityEventTagJpaEntity> extends EventEntityAbstractTagJpaEntityRepository<T> {
  default String getCode() {
    return "e";
  }
}
