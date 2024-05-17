package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import com.prosilion.superconductor.util.TagDtoFactory;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
import nostr.event.impl.GenericTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  public void saveGenericTags(List<BaseTag> singleLetterGenericTags) {
    saveTags(createDtos(selectReposByRelevantTags(singleLetterGenericTags)));
  }

  private List<Map<String, String>> selectReposByRelevantTags(List<BaseTag> baseTags) {
    return baseTags.stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(this::containsKey)
        .map(this::mapAtts)
        .toList();
  }

  private boolean containsKey(GenericTag tags) {
    return genericTagEntityRepositoryMap.containsKey(tags.getCode());
  }

  private Map<String, String> mapAtts(GenericTag gTag) {
    return Map.of(gTag.getCode(), gTag.getAttributes().get(0).getValue().toString());
  }

  private List<GenericTagDto> createDtos(List<Map<String, String>> list) {
    return list.stream().flatMap(tag -> tag.entrySet().stream()).map(this::createDtos).toList();
  }

  private GenericTagDto createDtos(Map.Entry<String, String> s) {
    return TagDtoFactory.createDto(s.getKey(), s.getValue());
  }

  private void saveTags(List<GenericTagDto> tags) {
    tags.stream().map(this::saveTag).forEach(this::saveJoins);
  }

  private GenericTagEntity saveTag(GenericTagDto tag) {
    return genericTagEntityRepositoryMap.get(tag.getCode()).save(tag.convertDtoToEntity());
  }

  private void saveJoins(GenericTagEntity tag) {
    joins.get(tag.getCode()).save(TagDtoFactory.createEntity(tag.getCode(), tag.getId(), tag.getId()));
  }
}