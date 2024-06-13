package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.RelaysTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelaysTagEntityRepository<T extends RelaysTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "relays";
  }
}