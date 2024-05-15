package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import com.prosilion.superconductor.entity.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import com.prosilion.superconductor.util.GenericTagValueMapper;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventEntityGenericTagEntityService<T extends GenericTagEntity> {
  private final Map<String, GenericTagEntityRepository<T>> repositoriesMap;
  private final Map<String, EventEntityGenericTagEntityRepository<T>> joins;

  @Autowired
  public EventEntityGenericTagEntityService(List<GenericTagEntityRepository<T>> repositories, List<EventEntityGenericTagEntityRepository<T>> joins) {
    this.repositoriesMap = new HashMap<>(repositories.stream().collect(
        Collectors.toMap(GenericTagEntityRepository::getCode, Function.identity())));
    this.joins = new HashMap<>(joins.stream().collect(
        Collectors.toMap(EventEntityGenericTagEntityRepository<T>::getKey, Function.identity())));
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
      savedIds.add(Optional.of(repositoriesMap.get(tagValueMapper.genericTag().getCode()).save(entity)).orElseThrow(NoResultException::new).getId());
    }
    return savedIds;
  }

  private void saveEventTags(String code, Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      joins.get(code).save(new EventEntityGeohashTagEntity(eventId, tagId));
    }
  }
}
