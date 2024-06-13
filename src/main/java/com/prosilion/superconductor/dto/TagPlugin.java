package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.AbstractTagEntity;
import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import nostr.event.BaseTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagPlugin<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>, // tag table
    R extends AbstractTagEntity, // tag to return
    S extends EventEntityAbstractTagEntity, // @MappedSuperclass for below
    T extends EventEntityAbstractTagEntityRepository<S>> // event -> tag join table
{
  String getCode();

  Class<R> getClazz();

  R convertDtoToEntity(P tag);

  AbstractTagDto getTagDto(P baseTag);

  S getEventEntityTagEntity(Long eventId, Long tagId);

  T getEventEntityStandardTagEntityRepositoryJoin();

  Q getStandardTagEntityRepositoryRxR();

  default List<R> getTags(Long eventId) {
    return getEventEntityStandardTagEntityRepositoryJoin()
        .getAllByEventId(eventId)
        .stream()
        .map(event -> Optional.of(
                getStandardTagEntityRepositoryRxR().findById(
                    event.getEventId()))
            .orElse(Optional.empty()).stream().toList())
        .flatMap(Collection::stream).toList();
  }

  default void saveTag(Long eventId, P baseTag) {
    getEventEntityStandardTagEntityRepositoryJoin().save(
        getEventEntityTagEntity(
            eventId,
            getStandardTagEntityRepositoryRxR().save(
                convertDtoToEntity(baseTag)).getId()));
  }
}
