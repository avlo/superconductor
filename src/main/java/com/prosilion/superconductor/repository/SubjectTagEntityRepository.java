package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagEntityRepository<T extends SubjectTagEntity> extends AbstractTagEntityRepository<T> {
  default String getCode() {
    return "subject";
  }
}