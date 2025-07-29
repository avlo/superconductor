package com.prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import com.prosilion.superconductor.lib.jpa.event.join.generic.GenericTagEntitiesService;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.EventEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.ConcreteTagEntitiesService;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventEntityService {
  private final ConcreteTagEntitiesService<BaseTag, AbstractTagEntityRepository<AbstractTagEntity>, AbstractTagEntity, EventEntityAbstractEntity, EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>> concreteTagEntitiesService;
  private final GenericTagEntitiesService genericTagEntitiesService;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityService(
      @NonNull ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagEntityRepository<AbstractTagEntity>,
          AbstractTagEntity,
          EventEntityAbstractEntity,
          EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>> concreteTagEntitiesService,
      @NonNull GenericTagEntitiesService genericTagEntitiesService,
      @NonNull EventEntityRepository eventEntityRepository) {
    this.concreteTagEntitiesService = concreteTagEntitiesService;
    this.genericTagEntitiesService = genericTagEntitiesService;
    this.eventEntityRepository = eventEntityRepository;
  }

  public Long saveEventEntity(@NonNull BaseEvent baseEvent) {
    return saveEventEntity(
        new GenericEventKindDto(baseEvent).convertBaseEventToEventIF());
  }

  public Long saveEventEntity(@NonNull EventIF genericEventKindIF) {
    try {
      Long savedEntityId = Optional.of(
              eventEntityRepository.save(
                  convertDtoToEntity(genericEventKindIF)))
          .map(EventEntityIF::getId)
          .orElseThrow(NoResultException::new);

      concreteTagEntitiesService.saveTags(savedEntityId, genericEventKindIF.getTags());
      genericTagEntitiesService.saveGenericTags(savedEntityId, genericEventKindIF.getTags());
      return savedEntityId;
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventEntityRepository.findByEventIdString(genericEventKindIF.getEventId()).orElseThrow(NoResultException::new).getId();
    }
  }

  public <T extends Long> Map<Kind, Map<T, EventEntityIF>> getAllMappedByKind() {
    Map<Kind, Map<T, EventEntityIF>> collect =
        getAll().stream()
            .collect(
                Collectors.groupingBy(EventIF::getKind,
                    Collectors.toMap(eventEntityIF ->
                            (T) eventEntityIF.getId(),
                        Function.identity())));
    return collect;
  }

  public List<EventEntityIF> getAll() {
    return eventEntityRepository.findAll().stream().map(this::populateEventEntity).collect(Collectors.toList());
  }

  public Optional<EventEntityIF> getEventByIdStringAsEventEntityIF(@NonNull String eventIdString) {
    Optional<EventEntityIF> entityIF = eventEntityRepository
        .findByEventIdString(eventIdString);
    return entityIF
        .map(this::populateEventEntity);
  }

  public Optional<EventEntityIF> findByEventIdString(@NonNull String eventIdString) {
    return getEventByIdStringAsEventEntityIF(eventIdString);
  }

  public List<EventEntityIF> getEventsByPublicKey(@NonNull PublicKey publicKey) {
    return eventEntityRepository.findByPubKey(
            publicKey.toHexString()).stream()
        .map(ee ->
            getEventById(ee.getId())).flatMap(Optional::stream).toList();
  }

  public List<EventEntityIF> getEventsByKind(@NonNull Kind kind) {
    return eventEntityRepository.findByKind(kind.getValue())
        .stream().map(ee ->
            getEventById(ee.getId())).flatMap(Optional::stream).toList();
  }

  public Optional<EventEntityIF> getEventById(@NonNull Long id) {
    return getById(id).map(this::populateEventEntity);
  }

  //  TODO: perhaps below as admin fxnality
  protected void deleteEventEntity(@NonNull EventEntityIF eventToDelete) {
//    concreteTagEntitiesService.deleteTags(eventToDelete.getId(), eventToDelete.getTags());
//    genericTagEntitiesService.deleteTags(eventToDelete.getTags());
//    eventEntityRepository.delete(convertDtoToEntity(eventToDelete));
  }

  private Optional<EventEntityIF> getById(Long id) {
    return eventEntityRepository.findByIdWoErasure(id)
        .map(EventEntityIF.class::cast);
  }

  private EventEntityIF populateEventEntity(EventEntityIF eventEntity) {
    List<BaseTag> concreteTags = concreteTagEntitiesService.getTags(eventEntity.getId()).stream().map(AbstractTagEntity::getAsBaseTag).toList();

    List<BaseTag> genericTags = genericTagEntitiesService.getGenericTags(eventEntity.getId()).stream().map(genericTag -> new GenericTag(genericTag.code(), genericTag.atts().stream().map(ElementAttributeDto::getElementAttribute).toList())).toList().stream().map(BaseTag.class::cast).toList();

    eventEntity.setTags(Stream.concat(concreteTags.stream(), genericTags.stream()).toList());

    return eventEntity;
  }

  public static EventEntity convertDtoToEntity(EventIF dto) {
    return new EventEntity(dto.getEventId(), dto.getKind().getValue(), dto.getPublicKey().toString(), dto.getCreatedAt(), dto.getSignature().toString(), dto.getContent());
  }
}
