package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import com.prosilion.superconductor.util.GenericTagValueMapper;
import jakarta.transaction.Transactional;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventEntityGenericTagEntityService<T extends GenericTagEntity> {
  private final Map<String, GenericTagEntityRepository<T>> genericTagEntityRepositoryMap;
  private final Map<String, EventEntityGenericTagEntityRepository<EventEntityGenericTagEntity>> joins;

  @Autowired
  public EventEntityGenericTagEntityService(List<GenericTagEntityRepository<T>> repositories, List<EventEntityGenericTagEntityRepository<EventEntityGenericTagEntity>> joins) {
    this.genericTagEntityRepositoryMap = repositories.stream().collect(
        Collectors.toMap(GenericTagEntityRepository::getCode, Function.identity()));
    this.joins = joins.stream().collect(
        Collectors.toMap(EventEntityGenericTagEntityRepository::getKey, Function.identity()));
  }

  public void saveGenericTags(GenericEvent event, Long id) {
    List<GenericTagValueMapper> tags = event.getTags().stream()
        .filter(genericTags -> Objects.equals("g", genericTags.getCode()))
        .map(o -> new GenericTagValueMapper(o.getCode(), o))
        .toList();

    List<Long> savedTagIds = saveTags(tags);
    tags.forEach(tag -> saveEventTags(tag.genericTag().getCode(), id, savedTagIds));
  }

  private List<Long> saveTags(List<GenericTagValueMapper> tags) {
    List<Long> savedIds = new ArrayList<>();
    for (GenericTagValueMapper tagValueMapper : tags) {
      GenericTagDto dto = new GeohashTagDto(tagValueMapper.code());
      dto.setCode(tagValueMapper.genericTag().getCode());
      T entity = dto.convertDtoToEntity();
      GenericTagEntityRepository<T> tGenericTagEntityRepository = genericTagEntityRepositoryMap.get(tagValueMapper.genericTag().getCode());
      savedIds.add(tGenericTagEntityRepository.save(entity));
    }
    return savedIds;
  }

  private void saveEventTags(String code, Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      EventEntityGenericTagEntity eventEntityGeohashTagEntity = new EventEntityGeohashTagEntity(eventId, tagId);
      joins.get(code).save(eventEntityGeohashTagEntity);
    }
  }
}
