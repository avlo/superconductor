package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import jakarta.transaction.Transactional;
import nostr.event.BaseTag;
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
public class EventEntityGenericTagEntityService {
  private final Map<String, GenericTagEntityRepository<GenericTagEntity>> genericTagEntityRepositoryMap;
  private final Map<String, EventEntityGenericTagEntityRepository<EventEntityGenericTagEntity>> joins;

  @Autowired
  public EventEntityGenericTagEntityService(List<GenericTagEntityRepository<GenericTagEntity>> repositories, List<EventEntityGenericTagEntityRepository<EventEntityGenericTagEntity>> joins) {
    this.genericTagEntityRepositoryMap = repositories.stream().collect(
        Collectors.toMap(GenericTagEntityRepository::getCode, Function.identity()));
    this.joins = joins.stream().collect(
        Collectors.toMap(EventEntityGenericTagEntityRepository::getKey, Function.identity()));
  }

  public void saveGenericTags(GenericEvent event, Long id) {
    List<Result> tags = event.getTags().stream()
        .filter(genericTags -> Objects.equals("g", genericTags.getCode()))
        .map(o -> new Result(o.getCode(), o))
        .toList();

    List<Long> savedTagIds = saveTags(tags);
    tags.forEach(tag -> saveJoins(tag.genericTag().getCode(), id, savedTagIds));
  }

  private List<Long> saveTags(List<Result> tags) {
    List<Long> savedIds = new ArrayList<>();
    for (Result tag : tags) {
      GenericTagDto dto = new GeohashTagDto(tag.code());
      dto.setCode(tag.genericTag().getCode());
      GenericTagEntity entity = dto.convertDtoToEntity();
      GenericTagEntityRepository<GenericTagEntity> genericTagEntityRepository = genericTagEntityRepositoryMap.get(tag.genericTag().getCode());
      savedIds.add(genericTagEntityRepository.save(entity));
    }
    return savedIds;
  }

  private void saveJoins(String code, Long eventId, List<Long> tagIds) {
    for (Long tagId : tagIds) {
      EventEntityGenericTagEntity join = new EventEntityGeohashTagEntity(eventId, tagId);
      joins.get(code).save(join);
    }
  }

  public record Result(String code, BaseTag genericTag) {
  }
}
