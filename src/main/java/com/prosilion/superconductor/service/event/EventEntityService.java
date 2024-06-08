package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import com.prosilion.superconductor.repository.EventEntityRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class EventEntityService<T extends GenericEvent> {
  private final EventEntityTagEntitiesService eventEntityTagEntitiesService;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityService(
      EventEntityTagEntitiesService eventEntityTagEntitiesService,
      EventEntityRepository eventEntityRepository) {
    this.eventEntityTagEntitiesService = eventEntityTagEntitiesService;
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
    eventEntityTagEntitiesService.saveTags(event, savedEntity.getId());
    return savedEntity.getId();
  }

  public EventDto getEventById(Long id) {
    return populateEventEntity(
        eventEntityRepository.findById(id).orElseThrow())
        .convertEntityToDto();
  }

//  public GenericEvent getEventByEventIdString(String eventId) {
//    Optional<EventEntity> byEventId = eventEntityRepository.findByEventId(eventId);
//    return getEventByEventId(byEventId.get());
//  }

  private @NotNull EventEntity populateEventEntity(EventEntity eventEntity) {
    List<BaseTag> baseTagsCast = eventEntityTagEntitiesService.getEventStandardTags(eventEntity.getId()).parallelStream().map(StandardTagEntity::getAsBaseTag).toList();
    List<BaseTag> genericTagCast = eventEntityTagEntitiesService.getEventGenericTags(eventEntity.getId()).parallelStream().map(GenericTagEntity::getAsBaseTag).toList();
    List<BaseTag> subjectTagCast = eventEntityTagEntitiesService.getEventSubjectTag(eventEntity.getId()).stream().map(SubjectTagEntity::getAsBaseTag).toList();
    eventEntity.setTags(Stream.of(baseTagsCast, genericTagCast, subjectTagCast).flatMap(Collection::stream).toList());
    return eventEntity;
  }

  public Map<Kind, Map<Long, T>> getAll() {
    return eventEntityRepository.findAll().parallelStream()
        .map(this::populateEventEntity)
        .collect(Collectors.groupingBy(eventEntity -> Kind.valueOf(eventEntity.getKind()),
            Collectors.toMap(EventEntity::getId, eventEntity -> (T) eventEntity.convertEntityToDto())));
  }
}
