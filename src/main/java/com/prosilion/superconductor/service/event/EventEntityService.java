package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.join.EventEntityAbstractTagEntity;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import com.prosilion.superconductor.repository.EventEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntityAbstractTagEntityRepository;
import com.prosilion.superconductor.repository.AbstractTagEntityRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventEntityService<T extends GenericEvent> {
  private final TagEntitiesService<
      BaseTag,
      AbstractTagEntityRepository<AbstractTagEntity>,
      AbstractTagEntity,
      EventEntityAbstractTagEntity,
      EventEntityAbstractTagEntityRepository<EventEntityAbstractTagEntity>>
      tagEntitiesService;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityService(
      TagEntitiesService<
          BaseTag,
          AbstractTagEntityRepository<AbstractTagEntity>,
          AbstractTagEntity,
          EventEntityAbstractTagEntity,
          EventEntityAbstractTagEntityRepository<EventEntityAbstractTagEntity>>
          tagEntitiesService,
      EventEntityRepository eventEntityRepository) {

    this.tagEntitiesService = tagEntitiesService;
    this.eventEntityRepository = eventEntityRepository;
  }

  protected Long saveEventEntity(GenericEvent event) {
    EventDto eventToSave = new EventDto(
        event.getPubKey(),
        event.getId(),
        Kind.valueOf(event.getKind()),
        event.getNip(),
        event.getCreatedAt(),
        event.getSignature(),
        event.getTags(),
        event.getContent()
    );

    EventEntity savedEntity = Optional.of(eventEntityRepository.save(eventToSave.convertDtoToEntity())).orElseThrow(NoResultException::new);
    tagEntitiesService.saveTags(savedEntity.getId(), event.getTags());
    return savedEntity.getId();
  }

  public Map<Kind, Map<Long, T>> getAll() {
    return eventEntityRepository.findAll().parallelStream()
        .map(this::populateEventEntity)
        .collect(Collectors.groupingBy(eventEntity -> Kind.valueOf(eventEntity.getKind()),
            Collectors.toMap(EventEntity::getId, EventEntity::convertEntityToDto)));
  }

  public T getEventById(Long id) {
    return populateEventEntity(
        getById(id).orElseThrow())
        .convertEntityToDto();
  }

  private @NotNull Optional<EventEntity> getById(Long id) {
    Optional<EventEntity> byId = eventEntityRepository.findById(id);
    return byId;
  }

  private @NotNull EventEntity populateEventEntity(EventEntity eventEntity) {
    List<BaseTag> baseTags = tagEntitiesService.getTags(eventEntity.getId()).parallelStream().map(AbstractTagEntity::getAsBaseTag).toList();
    eventEntity.setTags(baseTags);
    return eventEntity;
  }
  //  public GenericEvent getEventByEventIdString(String eventId) {
//    Optional<EventEntity> byEventId = eventEntityRepository.findByEventId(eventId);
//    return getEventByEventId(byEventId.get());
//  }
}