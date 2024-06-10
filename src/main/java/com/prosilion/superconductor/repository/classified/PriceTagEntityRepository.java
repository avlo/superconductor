package com.prosilion.superconductor.repository.classified;

import com.prosilion.superconductor.entity.classified.PriceTagEntity;
import com.prosilion.superconductor.repository.standard.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceTagEntityRepository<T extends PriceTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "price";
  }
}
