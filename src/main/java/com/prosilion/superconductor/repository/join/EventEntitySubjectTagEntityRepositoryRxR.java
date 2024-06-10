package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntitySubjectTagEntityRepositoryRxR<T extends EventEntitySubjectTagEntityRxR> extends EventEntityStandardTagEntityRepositoryRxR<T> {
  //  List<T> findFirstById(Long id);
  @Override
  default String getCode() {
    return "subject";
  }
}