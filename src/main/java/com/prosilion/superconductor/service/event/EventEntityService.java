package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import com.prosilion.superconductor.repository.EventEntityRepository;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Getter
@Service
public class EventEntityService {
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
    Optional<EventEntity> byEventId = eventEntityRepository.findById(id);
    return getEventByEventId(byEventId.orElseThrow());
  }

  private @NotNull EventDto getEventByEventId(EventEntity eventEntity) {
    return populateEventEntity(eventEntity).convertEntityToDto();
  }

  public GenericEvent getEventByEventIdString(String eventId) {
    Optional<EventEntity> byEventId = eventEntityRepository.findByEventId(eventId);
    return getEventByEventId(byEventId.orElseThrow());
  }

  private @NotNull EventEntity populateEventEntity(EventEntity eventEntity) {
    List<BaseTag> baseTagsCast = eventEntityTagEntitiesService.getEventStandardTags(eventEntity.getId()).stream().map(StandardTagEntity::getAsBaseTag).toList();
    List<BaseTag> genericTagCast = eventEntityTagEntitiesService.getEventGenericTags(eventEntity.getId()).stream().map(GenericTagEntity::getAsBaseTag).toList();
    List<BaseTag> subjectTagCast = eventEntityTagEntitiesService.getEventSubjectTag(eventEntity.getId()).stream().map(SubjectTagEntity::getAsBaseTag).toList();
    eventEntity.setTags(Stream.of(baseTagsCast, genericTagCast, subjectTagCast).flatMap(Collection::stream).toList());
    return eventEntity;
  }

  public List<EventEntity> getAll() {
    return eventEntityRepository.findAll().stream().map(this::populateEventEntity).toList();
  }
}
