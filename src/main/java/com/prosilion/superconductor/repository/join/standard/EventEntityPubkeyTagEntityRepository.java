package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPubkeyTagEntityRepository<T extends EventEntityPubkeyTagEntity> extends EventEntityStandardTagEntityRepository<T> {
  default Character getCode() {
    return 'p';
  }
}