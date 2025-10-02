package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import jakarta.transaction.Transactional;
import java.util.List;
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
                .map(EventEntityGenericTagJpaEntity::getGenericTagId).toList())
        .stream().map(genericTagJpaEntity ->
            genericTagJpaEntity.convertEntityToDto(
                service.getElementAttributeList(genericTagJpaEntity.getId()).stream().toList()))
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

  private GenericTagJpaEntity saveTag(GenericTagDto tag) {
    GenericTagJpaEntity save = repo.save(tag.convertDtoToEntity());
    service.saveElementAttributeList(save.getId(), tag.atts());
    return save;
  }

  private void saveJoins(Long eventId, GenericTagJpaEntity tag) {
    join.save(new EventEntityGenericTagJpaEntity(eventId, tag.getId()));
  }
}
