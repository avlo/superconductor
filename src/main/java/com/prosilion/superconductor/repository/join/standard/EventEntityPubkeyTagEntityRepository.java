package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntityRxR;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityPubkeyTagEntityRepository<T extends EventEntityPubkeyTagEntityRxR> extends EventEntityStandardTagEntityRepositoryRxR<T> {
  default String getCode() {
    return "p";
  }
}