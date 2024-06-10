package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDtoRxR;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import nostr.event.BaseTag;

import java.util.List;

public interface TagModule<
    P extends BaseTag,
    Q extends StandardTagEntityRepositoryRxR<R>, // tag table
    R extends StandardTagEntityRxR, // tag to return
    S extends EventEntityStandardTagEntityRxR, // event -> tag join table
    U extends EventEntityStandardTagEntityRepositoryRxR<S>> // join table within service
{
  String getCode();

  Class<R> getClazz();

  R convertDtoToEntity(P tag);

  StandardTagDtoRxR getTagDto(P baseTag);

  S getEventEntityTagEntity(Long eventId, Long tagId);

  U getEventEntityStandardTagEntityRepositoryJoin();

  Q getStandardTagEntityRepositoryRxR();

  default List<R> getTags(Long eventId) {
    return getEventEntityStandardTagEntityRepositoryJoin().getAllByEventId(eventId).parallelStream().map(joinId -> getStandardTagEntityRepositoryRxR().getReferenceById(eventId))
        .map(getClazz()::cast)
        .toList();
  }

  default void saveTag(Long eventId, P baseTag) {
    R savedRepoTag = getStandardTagEntityRepositoryRxR().save(convertDtoToEntity(baseTag));
    S savedJoinTag = getEventEntityStandardTagEntityRepositoryJoin().save(getEventEntityTagEntity(eventId, savedRepoTag.getId()));
//    savedJoinTag.
  }
}
