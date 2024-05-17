package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import com.prosilion.superconductor.util.TagDtoFactory;
import jakarta.transaction.Transactional;
import nostr.base.ElementAttribute;
import nostr.event.BaseTag;
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

  public void saveGenericTags(List<BaseTag> singleLetterGenericTags, Long id) {
    saveTags(getDtos(createResultMap(singleLetterGenericTags)));
  }

  private List<Map<String, String>> createResultMap(List<BaseTag> singleLetterGenericTags) {
    return singleLetterGenericTags.stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(tags -> genericTagEntityRepositoryMap.containsKey(tags.getCode()))
        .map(gTag -> {
          List<ElementAttribute> attributes = gTag.getAttributes();
          ElementAttribute elementAttribute = attributes.get(0);
          return Map.of(gTag.getCode(), elementAttribute.getValue().toString());
        })
        .toList();
  }

  private List<GenericTagDto> getDtos(List<Map<String, String>> list) {
    return list.stream()
        .flatMap(tag -> tag.entrySet().stream()).map(
            s -> {
              GenericTagDto dto = TagDtoFactory.createDto(s.getKey(), s.getValue());
              return dto;
            })
        .toList();
  }

  private void saveTags(List<GenericTagDto> tags) {
    List<Long> savedIds = new ArrayList<>();
    for (GenericTagDto tag : tags) {
      GenericTagEntity entity = tag.convertDtoToEntity();
      GenericTagEntity saved = genericTagEntityRepositoryMap.get(tag.getCode()).save(entity);

      EventEntityGenericTagEntity join = TagDtoFactory.createEntity(tag.getCode(), entity.getId(), saved.getId());
      joins.get(tag.getCode()).save(join);
      savedIds.add(saved.getId());
    }
  }
}