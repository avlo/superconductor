package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.standard.EventEntityEventTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityEventTagEntityRepository<T extends EventEntityEventTagEntity> extends EventEntityStandardTagEntityRepository<T> {
  default Character getCode() {
    return 'e';
  }
}