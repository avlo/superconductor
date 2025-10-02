package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.EventJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;

@Slf4j
public class EventJpaEntityService {
  private final ConcreteTagEntitiesService<
      BaseTag,
      AbstractTagJpaEntityRepository<AbstractTagJpaEntity>,
      AbstractTagJpaEntity,
      EventEntityAbstractJpaEntity,
      EventEntityAbstractTagJpaEntityRepository<EventEntityAbstractJpaEntity>> concreteTagEntitiesService;
  private final GenericTagJpaEntitiesService genericTagJpaEntitiesService;
  private final EventJpaEntityRepository eventJpaEntityRepository;

  public EventJpaEntityService(
      @NonNull ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagJpaEntityRepository<AbstractTagJpaEntity>,
          AbstractTagJpaEntity,
          EventEntityAbstractJpaEntity,
          EventEntityAbstractTagJpaEntityRepository<EventEntityAbstractJpaEntity>> concreteTagEntitiesService,
      @NonNull GenericTagJpaEntitiesService genericTagJpaEntitiesService,
      @NonNull EventJpaEntityRepository eventJpaEntityRepository) {
    this.concreteTagEntitiesService = concreteTagEntitiesService;
    this.genericTagJpaEntitiesService = genericTagJpaEntitiesService;
    this.eventJpaEntityRepository = eventJpaEntityRepository;
  }

  public Long saveEvent(@NonNull EventIF genericEventKindIF) {
    try {
      EventJpaEntity save = eventJpaEntityRepository.save(
          convertDtoToEntity(genericEventKindIF));
      Long savedEntityId = Optional.of(
              save)
          .map(EventJpaEntityIF::getUid)
          .orElseThrow(NoResultException::new);

      concreteTagEntitiesService.saveTags(savedEntityId, genericEventKindIF.getTags());
      genericTagJpaEntitiesService.saveGenericTags(savedEntityId, genericEventKindIF.getTags());
      return savedEntityId;
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventJpaEntityRepository.findByEventId(genericEventKindIF.getId()).orElseThrow(NoResultException::new).getUid();
    }
  }

  public List<EventJpaEntityIF> getAll() {
    return eventJpaEntityRepository.findAll().stream().map(this::populateEventJpaEntity).collect(Collectors.toList());
  }

  public Optional<EventJpaEntityIF> findByEventIdString(@NonNull String eventIdString) {
    Optional<EventJpaEntityIF> entityIF = eventJpaEntityRepository
        .findByEventId(eventIdString);
    return entityIF
        .map(this::populateEventJpaEntity);
  }

  public List<EventJpaEntityIF> getEventsByPublicKey(@NonNull PublicKey publicKey) {
    return eventJpaEntityRepository.findByPubKey(
            publicKey.toHexString()).stream()
        .map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  public List<EventJpaEntityIF> getEventsByKind(@NonNull Kind kind) {
    return eventJpaEntityRepository.findByKind(kind.getValue())
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  public Optional<EventJpaEntityIF> getEventByUid(@NonNull Long id) {
    return eventJpaEntityRepository.findByUid(id).map(this::populateEventJpaEntity);
  }

  //  TODO: perhaps below as admin fxnality
  protected void deleteEventEntity(@NonNull EventJpaEntityIF eventToDelete) {
//    concreteTagEntitiesService.deleteTags(eventToDelete.getUid(), eventToDelete.getTags());
//    genericTagEntitiesService.deleteTags(eventToDelete.getTags());
//    eventEntityRepository.delete(convertDtoToEntity(eventToDelete));
  }

  private EventJpaEntityIF populateEventJpaEntity(EventJpaEntityIF entityIF) {
    entityIF.setTags(
        Stream.concat(
            concreteTagEntitiesService.getTags(entityIF.getUid()).stream().map(AbstractTagJpaEntity::getAsBaseTag).toList().stream(),
            genericTagJpaEntitiesService.getGenericTags(entityIF.getUid()).stream().map(genericTag ->
                    new GenericTag(
                        genericTag.code(),
                        genericTag.atts().stream().map(ElementAttributeDto::getElementAttribute).toList())).toList().stream()
                .map(BaseTag.class::cast).toList().stream()).toList());
    return entityIF;
  }

  private EventJpaEntity convertDtoToEntity(EventIF dto) {
    return new EventJpaEntity(dto.getId(), dto.getKind().getValue(), dto.getPublicKey().toString(), dto.getCreatedAt(), dto.getSignature().toString(), dto.getContent());
  }
}
