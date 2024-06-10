package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntitySubjectTagEntityRepository<T extends EventEntitySubjectTagEntity> extends EventEntityStandardTagEntityRepository<T> {
  //  List<T> findFirstById(Long id);
  @Override
  default String getCode() {
    return "subject";
  }
}