package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPubkeyTagEntityRepository<T extends EventEntityPubkeyTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "p";
  }
}