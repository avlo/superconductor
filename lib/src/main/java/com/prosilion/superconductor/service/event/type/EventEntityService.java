package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.repository.EventEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import com.prosilion.superconductor.service.event.ConcreteTagEntitiesService;
import com.prosilion.superconductor.service.event.join.generic.GenericTagEntitiesService;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventEntityService<T extends GenericEventDtoIF> {
  private final ConcreteTagEntitiesService<
      BaseTag,
      AbstractTagEntityRepository<AbstractTagEntity>,
      AbstractTagEntity,
      EventEntityAbstractEntity,
      EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>>
      concreteTagEntitiesService;
  private final GenericTagEntitiesService genericTagEntitiesService;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityService(
      ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagEntityRepository<AbstractTagEntity>,
          AbstractTagEntity,
          EventEntityAbstractEntity,
          EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>>
          concreteTagEntitiesService,
      GenericTagEntitiesService genericTagEntitiesService,
      EventEntityRepository eventEntityRepository) {

    this.concreteTagEntitiesService = concreteTagEntitiesService;
    this.genericTagEntitiesService = genericTagEntitiesService;
    this.eventEntityRepository = eventEntityRepository;
  }

  public Long saveEventEntity(@NonNull BaseEvent event) {
    try {
      EventEntity savedEntity = Optional.of(
          eventEntityRepository.save(
              new EventDto(event).convertDtoToEntity())).orElseThrow(NoResultException::new);
      concreteTagEntitiesService.saveTags(savedEntity.getId(), event.getTags());
      genericTagEntitiesService.saveGenericTags(savedEntity.getId(), event.getTags());
      return savedEntity.getId();
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventEntityRepository.findByEventIdString(event.getId()).orElseThrow(NoResultException::new).getId();
    }
  }

  public Long saveEventEntity(@NonNull GenericEventDtoIF event) {
    try {
      EventEntity savedEntity = Optional.of(eventEntityRepository.save(convertDtoToEntity(event))).orElseThrow(NoResultException::new);
      concreteTagEntitiesService.saveTags(savedEntity.getId(), event.getTags());
      genericTagEntitiesService.saveGenericTags(savedEntity.getId(), event.getTags());
      return savedEntity.getId();
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventEntityRepository.findByEventIdString(event.getId()).orElseThrow(NoResultException::new).getId();
    }
  }

  public Map<Kind, Map<Long, T>> getAll() {
    return eventEntityRepository.findAll().stream()
        .map(this::populateEventEntity)
        .collect(Collectors.groupingBy(eventEntity -> Kind.valueOf(eventEntity.getKind()),
            Collectors.toMap(EventEntity::getId, EventEntity::convertEntityToDto)));
  }

  public Optional<EventEntity> findByEventIdString(@NonNull String eventIdString) {
    return eventEntityRepository.findByEventIdString(eventIdString);
  }

  public List<T> getEventsByPublicKey(@NonNull PublicKey publicKey) {
    return eventEntityRepository
        .findByPubKey(
            publicKey.toHexString())
        .stream().map(ee ->
            getEventById(ee.getId())).toList();
  }

  public List<T> getEventsByKind(@NonNull Kind kind) {
    return eventEntityRepository
        .findByKind(
            kind.getValue())
        .stream().map(ee ->
            getEventById(ee.getId())).toList();
  }

  public T getEventById(@NonNull Long id) {
    return populateEventEntity(
        getById(id)
            .orElseThrow())
        .convertEntityToDto();
  }

  //  TODO: perhaps below as admin fxnality
  protected void deleteEventEntity(@NonNull EventEntity eventToDelete) {
//    concreteTagEntitiesService.deleteTags(eventToDelete.getId(), eventToDelete.getTags());
    genericTagEntitiesService.deleteTags(eventToDelete.getTags());
    eventEntityRepository.delete(eventToDelete);
  }

  private Optional<EventEntity> getById(Long id) {
    return eventEntityRepository.findById(id);
  }

  private EventEntity populateEventEntity(EventEntity eventEntity) {
    List<BaseTag> concreteTags = concreteTagEntitiesService.getTags(
            eventEntity.getId()).stream()
        .map(AbstractTagEntity::getAsBaseTag).toList();

    List<BaseTag> genericTags = genericTagEntitiesService.getGenericTags(
            eventEntity.getId()).stream()
        .map(genericTag ->
            new GenericTag(genericTag.code(), genericTag.atts().stream()
                .map(ElementAttributeDto::getElementAttribute).toList())).toList().stream()
        .map(BaseTag.class::cast).toList();

    eventEntity.setTags(Stream.concat(concreteTags.stream(), genericTags.stream()).toList());
    return eventEntity;
  }

  public static EventEntity convertDtoToEntity(GenericEventDtoIF dto) {
    return new EventEntity(
        dto.getId(),
        dto.getKind().getValue(),
        dto.getPublicKey().toString(),
        dto.getCreatedAt(),
        dto.getSignature().toString(),
        dto.getContent());
  }
}
