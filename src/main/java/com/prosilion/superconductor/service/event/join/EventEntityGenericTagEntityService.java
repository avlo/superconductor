package com.prosilion.superconductor.service.event.join;

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
    List<Map<String, String>> map = createResultMap(event);
    List<GeohashTagDto> dtos = getDtos(map);
    List<Long> savedTagIds = saveTags(dtos);
    dtos.forEach(tag -> saveJoins(tag.getCode(), id, savedTagIds));
  }

  private List<Map<String, String>> createResultMap(GenericEvent event) {
    List<Map<String, String>> maps = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(tags -> tags.getCode().equalsIgnoreCase("g"))
        .map(gTag -> {
          List<ElementAttribute> attributes = gTag.getAttributes();
          ElementAttribute elementAttribute = attributes.get(0);
          return Map.of(gTag.getCode(), elementAttribute.getValue().toString());
        })
        .toList();
    return maps;
  }

  private List<GeohashTagDto> getDtos(List<Map<String, String>> map) {
    return map.stream().collect(Collectors.toSet()).stream().map(dto -> {
      String g = dto.get("g");
      GeohashTagDto geohashTagDto = new GeohashTagDto(g);
      return geohashTagDto;
    }).toList();
  }

  private List<Long> saveTags(List<GeohashTagDto> tags) {
    List<Long> savedIds = new ArrayList<>();
    for (GeohashTagDto tag : tags) {
      GenericTagEntity entity = tag.convertDtoToEntity();
      GenericTagEntityRepository<GenericTagEntity> genericTagEntityRepository = genericTagEntityRepositoryMap.get(tag.getCode());
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
}