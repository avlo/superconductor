package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PubkeyTagEntityRepository<T extends PubkeyTagEntity> extends StandardTagEntityRepository<T> {
  default String getCode() {
    return "p";
  }
}