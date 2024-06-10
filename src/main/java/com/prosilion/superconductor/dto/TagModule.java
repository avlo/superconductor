package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.service.event.join.standard.EventEntityStandardTagEntityServiceIFRxR;
import nostr.event.BaseTag;

import java.util.List;

public interface TagModule<
    P extends BaseTag,
    Q extends StandardTagEntityRepositoryRxR<R>, // tag table
    R extends StandardTagEntityRxR, // tag to return
    S extends EventEntityStandardTagEntityRxR, // event -> tag join table
    T extends EventEntityStandardTagEntityServiceIFRxR<R, S>, // event -> tag join service
    U extends EventEntityStandardTagEntityRepositoryRxR<S>> // join table within service
{
  String getCode();

  void setBaseTag(P baseTag);

  R convertDtoToEntity();

  StandardTagDtoRxR getTagDto();

  S getEventEntityTagEntity(Long eventId, Long tagId);

  U getEventEntityStandardTagEntityRepositoryJoin();

  T getEventEntityStandardTagEntityServiceRxR();

  Q getStandardTagEntityRepositoryRxR();

  default List<R> getTags(Long eventId) {
    return getEventEntityStandardTagEntityRepositoryJoin().getAllByEventId(eventId).parallelStream().map(joinId -> getEventEntityStandardTagEntityServiceRxR().getJoin().getAllByEventId(eventId))
        .map(getEventEntityStandardTagEntityServiceRxR().getClazz()::cast)
        .toList();
  }

  default void saveTags(Long eventId, P baseTag) {

  }
}
