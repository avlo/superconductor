package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepository;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepository;
import nostr.event.BaseTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TagModule<
    P extends BaseTag,
    Q extends StandardTagEntityRepository<R>, // tag table
    R extends StandardTagEntity, // tag to return
    S extends EventEntityStandardTagEntity, // event -> tag join table
    U extends EventEntityStandardTagEntityRepository<S>> // join table within service
{
  String getCode();

  Class<R> getClazz();

  R convertDtoToEntity(P tag);

  StandardTagDto getTagDto(P baseTag);

  S getEventEntityTagEntity(Long eventId, Long tagId);

  U getEventEntityStandardTagEntityRepositoryJoin();

  Q getStandardTagEntityRepositoryRxR();

  default List<R> getTags(Long eventId) {
    Stream<List<R>> listStream = getEventEntityStandardTagEntityRepositoryJoin()
        .getAllByEventId(eventId)
        .stream()
        .map(event -> Optional.of(
                getStandardTagEntityRepositoryRxR().findById(
                    event.getEventId()))
            .orElse(Optional.empty()).stream().toList());
    List<R> list = listStream.flatMap(Collection::stream).toList();
    return list;
  }

  default void saveTag(Long eventId, P baseTag) {
    R savedRepoTag = getStandardTagEntityRepositoryRxR().save(convertDtoToEntity(baseTag));
    S savedJoinTag = getEventEntityStandardTagEntityRepositoryJoin().save(getEventEntityTagEntity(eventId, savedRepoTag.getId()));
//    savedJoinTag.
  }
}
