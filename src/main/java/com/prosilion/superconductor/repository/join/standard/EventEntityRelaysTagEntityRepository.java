package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityRelaysTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityRelaysTagEntityRepository<T extends EventEntityRelaysTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "relays";
  }
}