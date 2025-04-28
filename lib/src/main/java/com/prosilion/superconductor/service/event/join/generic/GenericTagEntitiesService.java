package com.prosilion.superconductor.service.event.join.generic;

import com.prosilion.superconductor.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.dto.generic.GenericTagDto;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.tag.GenericTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
public class GenericTagEntitiesService {
  private final GenericTagEntityElementAttributeEntityService service;
  private final GenericTagEntityRepository repo;
  private final EventEntityGenericTagEntityRepository join;

  @Autowired
  public GenericTagEntitiesService(
      GenericTagEntityElementAttributeEntityService genericTagEntityElementAttributeEntityService,
      GenericTagEntityRepository genericTagEntityRepository,
      EventEntityGenericTagEntityRepository join) {
    this.service = genericTagEntityElementAttributeEntityService;
    this.repo = genericTagEntityRepository;
    this.join = join;
  }

  public List<GenericTagDto> getGenericTags(@NonNull Long eventId) {
    return repo.findAllById(
            join.findByEventId(eventId).stream()
                .map(EventEntityGenericTagEntity::getGenericTagId).toList())
        .stream().map(genericTagEntity ->
            genericTagEntity.convertEntityToDto(
                service.getElementAttributeList(genericTagEntity.getId()).stream().toList()))
        .toList();
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
    assert(false);
  }

  public void deleteTags(@NonNull Long eventId) {
    assert(false);
  }

  private GenericTagEntity saveTag(GenericTagDto tag) {
    GenericTagEntity save = repo.save(tag.convertDtoToEntity());
    service.saveElementAttributeList(save.getId(), tag.atts());
    return save;
  }

  private void saveJoins(Long eventId, GenericTagEntity tag) {
    join.save(new EventEntityGenericTagEntity(eventId, tag.getId()));
  }
}
