package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityExternalIdentityTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityExternalIdentityTagJpaEntityRepository<T extends EventEntityExternalIdentityTagJpaEntity> extends EventEntityAbstractTagJpaEntityRepository<T> {
  default String getCode() {
    return "i";
  }
}
