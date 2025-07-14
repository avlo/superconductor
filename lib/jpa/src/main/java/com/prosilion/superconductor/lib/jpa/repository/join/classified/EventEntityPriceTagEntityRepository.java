package com.prosilion.superconductor.lib.jpa.repository.join.classified;

import com.prosilion.superconductor.lib.jpa.entity.join.classified.EventEntityPriceTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPriceTagEntityRepository<T extends EventEntityPriceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "price";
  }
}
