package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import jakarta.transaction.Transactional;
import nostr.base.ElementAttribute;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventEntityGenericTagEntityService {
  private final Map<String, GenericTagEntityRepository<GenericTagEntity>> genericTagEntityRepositoryMap;
  private final Map<String, EventEntityGenericTagEntityRepository<EventEntityGenericTagEntity>> joins;

  @Autowired
  public EventEntityGenericTagEntityService(
      List<GenericTagEntityRepository<GenericTagEntity>> repositories,
      List<EventEntityGenericTagEntityRepository<EventEntityGenericTagEntity>> joins) {
    this.genericTagEntityRepositoryMap = repositories.stream().collect(
        Collectors.toMap(GenericTagEntityRepository::getCode, Function.identity()));
    this.joins = joins.stream().collect(
        Collectors.toMap(EventEntityGenericTagEntityRepository::getCode, Function.identity()));
  }

  public void saveGenericTags(GenericEvent event, Long id) {
    List<GenericTag> genericTagsOnly = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast).toList();

    List<List<ElementAttribute>> gTags = genericTagsOnly.stream()
        .filter(tag -> tag.getCode().equalsIgnoreCase("g")).map(GenericTag::getAttributes).toList();

    List<GenericTagResult> tags = gTags.stream()
        .map(elementAttribute -> new GenericTagResult(elementAttribute.get(0).getName(), elementAttribute.get(0).getValue().toString()))
        .toList();

    List<Long> savedTagIds = saveTags(tags);
    tags.forEach(tag -> saveJoins(tag.code(), id, savedTagIds));
  }

  private List<Long> saveTags(List<GenericTagResult> tags) {
    List<Long> savedIds = new ArrayList<>();
    for (GenericTagResult tag : tags) {
      GenericTagDto dto = new GeohashTagDto(tag.location());
      GenericTagEntity entity = dto.convertDtoToEntity();
      GenericTagEntityRepository<GenericTagEntity> genericTagEntityRepository = genericTagEntityRepositoryMap.get(tag.code());
      GenericTagEntity saved = genericTagEntityRepository.save(entity);
      savedIds.add(saved.getId());
    }
    return savedIds;
  }

  private void saveJoins(String code, Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      EventEntityGenericTagEntity join = new EventEntityGeohashTagEntity(eventId, tagId);
      joins.get(code).save(join);
    }
  }

  public record GenericTagResult(String code, String location) {
  }
}
