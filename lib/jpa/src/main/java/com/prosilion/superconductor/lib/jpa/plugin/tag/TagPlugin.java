package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.BaseTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;

public interface TagPlugin<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>, // dto table
    R extends AbstractTagEntity, // dto to return
    S extends EventEntityAbstractEntity, // @MappedSuperclass for below
    T extends EventEntityAbstractTagEntityRepository<S>> // event -> dto join table
{
  String getCode();
  AbstractTagDto getTagDto(P baseTag);
  AbstractTagEntityRepository<R> getRepo();
  EventEntityAbstractTagEntityRepository<S> getJoin();
  S getEventEntityTagEntity(Long eventId, Long baseTagId);

  default R convertDtoToEntity(@NonNull P baseTag) {
    return (R) getTagDto(baseTag).convertDtoToEntity();
  }

  default List<R> getTags(@NonNull Long eventId) {
    return getJoin()
        .findByEventId(eventId)
        .stream()
        .map(event -> Optional.of(
                getRepo().findById(
                    event.getId()))
            .orElseGet(Optional::empty).stream().toList())
        .flatMap(Collection::stream).distinct().toList();
  }

  default void saveTag(@NonNull Long eventId, @NonNull P baseTag) {
    getJoin().save(
        getEventEntityTagEntity(
            eventId,
            getRepo().save(
                convertDtoToEntity(baseTag)).getId()));
  }
}
