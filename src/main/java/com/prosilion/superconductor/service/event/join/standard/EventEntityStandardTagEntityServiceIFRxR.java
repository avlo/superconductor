package com.prosilion.superconductor.service.event.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import nostr.event.impl.GenericEvent;

import java.util.List;

public interface EventEntityStandardTagEntityServiceIFRxR<T extends StandardTagEntityRxR, U extends EventEntityStandardTagEntityRxR> {

  List<T> getTags(Long eventId);

  void saveTags(GenericEvent event, Long id);

  EventEntityStandardTagEntityRepositoryRxR<U> getJoin();

}