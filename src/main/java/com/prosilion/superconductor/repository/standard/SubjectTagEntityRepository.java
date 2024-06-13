package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.SubjectTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagEntityRepository<T extends SubjectTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "subject";
  }
}