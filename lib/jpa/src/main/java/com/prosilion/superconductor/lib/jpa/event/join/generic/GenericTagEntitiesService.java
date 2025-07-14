package com.prosilion.superconductor.lib.jpa.event.join.generic;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.dto.generic.GenericTagDto;
import com.prosilion.superconductor.lib.jpa.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.lib.jpa.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.generic.EventEntityGenericTagEntityRepository;

@Slf4j
@Transactional
public class GenericTagEntitiesService {
  private final GenericTagEntityElementAttributeEntityService service;
  private final GenericTagEntityRepository repo;
  private final EventEntityGenericTagEntityRepository join;

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
    assert (false);
  }

  public void deleteTags(@NonNull Long eventId) {
    assert (false);
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
