package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.BaseTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;

public interface TagPlugin<
    P extends BaseTag,
    Q extends AbstractTagJpaEntityRepository<R>, // dto table
    R extends AbstractTagJpaEntity, // dto to return
    S extends EventEntityAbstractJpaEntity, // @MappedSuperclass for below
    T extends EventEntityAbstractTagJpaEntityRepository<S>> // event -> dto join table
{
  String getCode();
  AbstractTagDto getTagDto(P baseTag);
  AbstractTagJpaEntityRepository<R> getRepo();
  EventEntityAbstractTagJpaEntityRepository<S> getJoin();
  S getEventEntityTagJpaEntity(Long eventId, Long baseTagId);

  default R convertDtoToJpaEntity(@NonNull P baseTag) {
    return (R) getTagDto(baseTag).convertDtoToJpaEntity();
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
        getEventEntityTagJpaEntity(
            eventId,
            getRepo().save(
                convertDtoToJpaEntity(baseTag)).getId()));
  }
}
