package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.HashtagTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagTagEntityRepository<T extends HashtagTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "t";
  }
}