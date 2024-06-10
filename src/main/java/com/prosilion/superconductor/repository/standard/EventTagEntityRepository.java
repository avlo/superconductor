package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.EventTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTagEntityRepository<T extends EventTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "e";
  }
}