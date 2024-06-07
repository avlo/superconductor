package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.standard.EventTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTagEntityRepository<T extends EventTagEntity> extends StandardTagEntityRepository<T> {
  default Character getCode() {
    return 'e';
  }
}