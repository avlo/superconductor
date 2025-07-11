package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityAddressTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityAddressTagEntityRepository<T extends EventEntityAddressTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  default String getCode() {
    return "a";
  }
}
