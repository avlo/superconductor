package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectTagEntityRepositoryRxR <T extends SubjectTagEntityRxR> extends StandardTagEntityRepositoryRxR<T> {
  default String getCode() {
    return "subject";
  }
}