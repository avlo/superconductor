package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityGeohashTagEntityRepository<T extends EventEntityGeohashTagEntity> extends EventEntityGenericTagEntityRepository<T> {
  default String getCode() {
    return "g";
  }
}