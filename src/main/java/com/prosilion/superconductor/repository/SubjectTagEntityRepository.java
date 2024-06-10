package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagEntityRepository<T extends SubjectTagEntity> extends StandardTagEntityRepository<T> {
  default String getCode() {
    return "subject";
  }
}