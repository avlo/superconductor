package com.prosilion.superconductor.service.event.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import nostr.event.impl.GenericEvent;

public interface EventEntityStandardTagEntityServiceIFRxR<T extends StandardTagEntityRxR, U extends EventEntityStandardTagEntityRxR> {

  void saveTags(GenericEvent event, Long id);

  EventEntityStandardTagEntityRepositoryRxR<U> getJoin();

  Class<T> getClazz();
}