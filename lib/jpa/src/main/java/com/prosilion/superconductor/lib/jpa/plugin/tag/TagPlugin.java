package com.prosilion.superconductor.lib.jpa.plugin.tag;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.BaseTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.ConcreteTagDto;
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
  Function<P, R> getEntityFactory();
  BiFunction<Long, Long, S> getJoinFactory();
  AbstractTagJpaEntityRepository<R> getRepo();
  EventEntityAbstractTagJpaEntityRepository<S> getJoin();

  default AbstractTagDto getTagDto(P baseTag) {
    return new ConcreteTagDto<>(baseTag, getEntityFactory());
  }

  default S getEventEntityTagJpaEntity(Long eventId, Long tagId) {
    return getJoinFactory().apply(eventId, tagId);
  }

  default R convertDtoToJpaEntity(@NonNull P baseTag) {
    return getEntityFactory().apply(baseTag);
  }

  default List<R> getTags(@NonNull Long eventId) {
    return getRepo().findAllById(
        getJoin().findByEventId(eventId).stream()
            .map(EventEntityAbstractJpaEntity::getTagId)
            .distinct()
            .toList()
    );
  }

  default Map<Long, List<R>> getTagsByEventIds(@NonNull Collection<Long> eventIds) {
    Map<Long, List<Long>> eventToTagIds = getJoin().findByEventIdIn(eventIds).stream()
        .collect(Collectors.groupingBy(
            EventEntityAbstractJpaEntity::getEventId,
            Collectors.mapping(EventEntityAbstractJpaEntity::getTagId, Collectors.toList())));

    List<Long> allTagIds = eventToTagIds.values().stream()
        .flatMap(List::stream).distinct().collect(Collectors.toList());

    Map<Long, R> tagsById = getRepo().findAllById(allTagIds).stream()
        .collect(Collectors.toMap(AbstractTagJpaEntity::getId, Function.identity()));

    Map<Long, List<R>> result = new HashMap<>();
    eventToTagIds.forEach((eventId, tagIds) ->
        result.put(eventId, tagIds.stream()
            .map(tagsById::get)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList())));
    return result;
  }

  default void saveTag(@NonNull Long eventId, @NonNull P baseTag) {
    getJoin().save(
        getEventEntityTagJpaEntity(
            eventId,
            getRepo().save(
                convertDtoToJpaEntity(baseTag)).getId()));
  }
}
