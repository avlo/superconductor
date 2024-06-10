package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.PubkeyTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PubkeyTagEntityRepository<T extends PubkeyTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "p";
  }
}