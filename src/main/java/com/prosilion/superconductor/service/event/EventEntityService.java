package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
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

  public GenericEvent getEventByEventIdString(String eventId) {
    Optional<EventEntity> byEventId = eventEntityRepository.findByEventId(eventId);
    return getEventByEventId(byEventId.orElseThrow());
  }

  private @NotNull EventDto getEventByEventId(EventEntity byEventId) {
    List<StandardTagEntity> eventStandardTags = eventEntityTagEntitiesService.getEventStandardTags(byEventId.getId());
    List<GenericTagEntity> eventGenericTags = eventEntityTagEntitiesService.getEventGenericTags(byEventId.getId());
//    Optional<SubjectTagEntity> eventSubjectTags = eventEntityTagEntityService.getEventSubjectTags(byEventId.getId());

//    List<StandardTagDto> standardTagDtoList = eventStandardTags.stream().map(StandardTagEntity::convertEntityToDto).toList();
//    List<GenericTagDto> genericTagDtoList = eventGenericTags.stream().map(GenericTagEntity::convertEntityToDto).toList();
//    List<SubjectTagDto> subjectTagDtoList = eventSubjectTags.stream().map(SubjectTagEntity::convertEntityToDto).toList();

    List<BaseTag> baseTagsCast = eventStandardTags.stream().map(StandardTagEntity::getAsBaseTag).toList();
    List<BaseTag> genericTagCast = eventGenericTags.stream().map(BaseTag.class::cast).toList();
//    List<BaseTag> subjectTagCast = subjectTagDtoList.stream().map(BaseTag.class::cast).toList();

    List<BaseTag> list = Stream.of(
        baseTagsCast,
        genericTagCast
//        ,subjectTagCast
    ).flatMap(Collection::stream).toList();

    byEventId.setTags(list);

    EventDto eventDto = byEventId.convertEntityToDto();
    return eventDto;
  }

  public List<EventEntity> getAll() {
    return eventEntityRepository.findAll();
  }
}
