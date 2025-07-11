package com.prosilion.superconductor.base.repository.join.classified;

import com.prosilion.superconductor.base.entity.join.classified.EventEntityPriceTagEntity;
import com.prosilion.superconductor.base.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPriceTagEntityRepository<T extends EventEntityPriceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "price";
  }
}
