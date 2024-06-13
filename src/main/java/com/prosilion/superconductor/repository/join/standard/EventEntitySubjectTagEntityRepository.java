package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntitySubjectTagEntityRepository<T extends EventEntitySubjectTagEntity> extends EventEntityAbstractTagEntityRepository<T> {
  //  List<T> findFirstById(Long id);
  @Override
  default String getCode() {
    return "subject";
  }
}