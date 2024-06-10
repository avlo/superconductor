package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import nostr.event.BaseTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TagModule<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>, // tag table
    R extends AbstractTagEntity, // tag to return
    // TODO: below superfluous?
    S extends EventEntityAbstractTagEntity, // @MappedSuperclass for below
    U extends EventEntityAbstractTagEntityRepository<S>> // event -> tag join table
{
  String getCode();

  Class<R> getClazz();

  R convertDtoToEntity(P tag);

  AbstractTagDto getTagDto(P baseTag);

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
