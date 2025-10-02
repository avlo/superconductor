package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityPubkeyTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPubkeyTagJpaEntityRepository<T extends EventEntityPubkeyTagJpaEntity> extends EventEntityAbstractTagJpaEntityRepository<T> {
  default String getCode() {
    return "p";
  }
}
