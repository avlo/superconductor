package com.prosilion.superconductor.lib.jpa.repository.join.classified;

import com.prosilion.superconductor.lib.jpa.entity.join.classified.EventEntityPriceTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPriceTagJpaEntityRepository<T extends EventEntityPriceTagJpaEntity> extends EventEntityAbstractTagJpaEntityRepository<T> {
  default String getCode() {
    return "price";
  }
}
