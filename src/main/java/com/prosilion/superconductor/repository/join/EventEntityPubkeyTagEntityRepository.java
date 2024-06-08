package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPubkeyTagEntityRepository<T extends EventEntityPubkeyTagEntity> extends EventEntityStandardTagEntityRepository<T> {
  default Character getCode() {
    return 'p';
  }
}