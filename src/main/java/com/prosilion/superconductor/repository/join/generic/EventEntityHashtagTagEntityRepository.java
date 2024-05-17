package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityHashtagTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityHashtagTagEntityRepository<T extends EventEntityHashtagTagEntity> extends EventEntityGenericTagEntityRepository<T> {
  default Character getCode() {
    return 't';
  }
}