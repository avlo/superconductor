package com.prosilion.superconductor.plugin.tag;

import com.prosilion.superconductor.dto.AbstractTagDto;
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
    Q extends AbstractTagEntityRepository<R>, // entity table
    R extends AbstractTagEntity, // from an entity table
    S extends EventEntityAbstractTagEntity, // from a join table
    T extends EventEntityAbstractTagEntityRepository<S>>  // join table
{
  String getCode();

  R convertDtoToEntity(P tag);

  AbstractTagDto getTagDto(P baseTag);

  S getEventEntityTagEntity(Long eventId, Long tagId);

  T getEventEntityStandardTagEntityRepositoryJoin();

  Q getStandardTagEntityRepository();

  default List<R> getTags(Long eventId) {
    return getEventEntityStandardTagEntityRepositoryJoin().findByEventId(eventId)
        .stream()
        .map(event -> Optional.of(
                getStandardTagEntityRepository().findById(
                    event.getId()))
            .orElseGet(Optional::empty).stream().toList())
        .flatMap(Collection::stream).distinct().toList();
  }

  default void saveTag(Long eventId, P baseTag) {
    getEventEntityStandardTagEntityRepositoryJoin().save(
        getEventEntityTagEntity(
            eventId,
            getStandardTagEntityRepository().save(
                convertDtoToEntity(baseTag)).getId()));
  }

  default void deleteTag(Long eventId, P tag) {
    R rTag = convertDtoToEntity(tag);
    getEventEntityStandardTagEntityRepositoryJoin().delete(
        getEventEntityTagEntity(eventId, rTag.getId()));
    getStandardTagEntityRepository().delete(rTag);
  }
}
