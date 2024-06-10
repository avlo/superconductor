package com.prosilion.superconductor.repository.join.classified;

import com.prosilion.superconductor.entity.join.classified.EventEntityPriceTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPriceTagEntityRepository<T extends EventEntityPriceTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "price";
  }
}