package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.dto.generic.GenericTagDto;
import com.prosilion.superconductor.lib.jpa.entity.generic.GenericTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.generic.EventEntityGenericTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.generic.GenericTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.generic.EventEntityGenericTagEntityRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class GenericTagJpaEntitiesService {
  private final GenericTagEntityElementAttributeJpaEntityService service;
  private final GenericTagJpaEntityRepository repo;
  private final EventEntityGenericTagEntityRepository join;

  @Autowired
  public GenericTagJpaEntitiesService(
      GenericTagEntityElementAttributeJpaEntityService genericTagEntityElementAttributeJpaEntityService,
      GenericTagJpaEntityRepository genericTagJpaEntityRepository,
      EventEntityGenericTagEntityRepository join) {
    this.service = genericTagEntityElementAttributeJpaEntityService;
    this.repo = genericTagJpaEntityRepository;
    this.join = join;
  }

  public List<GenericTagDto> getGenericTags(@NonNull Long eventId) {
    return repo.findAllById(
            join.findByEventId(eventId).stream()
                .map(EventEntityGenericTagJpaEntity::getTagId).toList())
        .stream().map(genericTagJpaEntity ->
            genericTagJpaEntity.convertEntityToDto(
                service.getElementAttributeList(genericTagJpaEntity.getId()).stream().toList()))
        .toList();
  }

  public Map<Long, List<GenericTagDto>> getGenericTagsByEventIds(@NonNull Collection<Long> eventIds) {
    Map<Long, List<Long>> eventToGenericTagIds = join.findByEventIdIn(eventIds).stream()
        .collect(Collectors.groupingBy(
            EventEntityGenericTagJpaEntity::getEventId,
            Collectors.mapping(EventEntityGenericTagJpaEntity::getTagId, Collectors.toList())));

    List<Long> allGenericTagIds = eventToGenericTagIds.values().stream()
        .flatMap(List::stream).distinct().collect(Collectors.toList());

    Map<Long, GenericTagJpaEntity> tagsById = repo.findAllById(allGenericTagIds).stream()
        .collect(Collectors.toMap(GenericTagJpaEntity::getId, Function.identity()));

    Map<Long, List<GenericTagDto>> result = new HashMap<>();
    eventToGenericTagIds.forEach((eventId, tagIds) ->
        result.put(eventId, tagIds.stream()
            .map(tagsById::get)
            .filter(Objects::nonNull)
            .map(entity -> entity.convertEntityToDto(
                service.getElementAttributeList(entity.getId())))
            .collect(Collectors.toList())));
    return result;
  }

  public void saveGenericTags(@NonNull Long eventId, @NonNull List<BaseTag> tags) {
    tags.stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .map(tag ->
            new GenericTagDto(tag.getCode(),
                tag.getAttributes().stream().map(ElementAttributeDto::new).toList()))
        .map(
            this::saveTag)
        .forEach(tag -> saveJoins(eventId, tag));
  }

  public void deleteTags(@NonNull List<BaseTag> tags) {
    assert (false);
  }

  public void deleteTags(@NonNull Long eventId) {
    assert (false);
  }

  private GenericTagJpaEntity saveTag(GenericTagDto tag) {
    GenericTagJpaEntity save = repo.save(tag.convertDtoToEntity());
    service.saveElementAttributeList(save.getId(), tag.atts());
    return save;
  }

  private void saveJoins(Long eventId, GenericTagJpaEntity tag) {
    join.save(new EventEntityGenericTagJpaEntity(eventId, tag.getId()));
  }
}
