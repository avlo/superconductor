package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.service.event.join.standard.EventEntityStandardTagEntityServiceIFRxR;
import nostr.event.BaseTag;

public interface TagModule<
    P extends BaseTag,
    Q extends StandardTagEntityRepositoryRxR<R>,
    R extends StandardTagEntityRxR,
    S extends EventEntityStandardTagEntityRxR,
    T extends EventEntityStandardTagEntityServiceIFRxR<R, S>,
    U extends EventEntityStandardTagEntityRepositoryRxR<S>> {
  String getCode();

  void setBaseTag(P baseTag);

  BaseTag getBaseTag();

  R convertDtoToEntity();

  StandardTagDtoRxR getTagDto();

  S getEventEntityTagEntity(Long eventId, Long tagId);

  U getEventEntityStandardTagEntityRepositoryJoin();

  T getEventEntityStandardTagEntityServiceRxR();

  Q getStandardTagEntityRepositoryRxR();
}
