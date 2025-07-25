package com.prosilion.superconductor.lib.jpa.repository.join.standard;

import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityPubkeyTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPubkeyTagEntityRepository<T extends EventEntityPubkeyTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "p";
  }
}
