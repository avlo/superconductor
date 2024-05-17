package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.HashtagTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface HashtagTagEntityRepository<T extends HashtagTagEntity> extends GenericTagEntityRepository<T> {
  default Character getCode() {
    return 't';
  }
}