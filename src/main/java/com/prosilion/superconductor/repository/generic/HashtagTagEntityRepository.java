package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.HashtagTagEntity;
import com.prosilion.superconductor.repository.standard.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagTagEntityRepository<T extends HashtagTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "t";
  }
}