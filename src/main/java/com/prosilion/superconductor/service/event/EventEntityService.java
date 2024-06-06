package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.BaseTagDto;
import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.SubjectTagDto;
import com.prosilion.superconductor.entity.BaseTagEntity;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
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
  private final EventEntityTagEntityService eventEntityTagEntityService;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityService(
      EventEntityTagEntityService eventEntityTagEntityService,
      EventEntityRepository eventEntityRepository) {
    this.eventEntityTagEntityService = eventEntityTagEntityService;
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
    eventEntityTagEntityService.saveTags(event, savedEntity.getId());
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
    List<BaseTagEntity> eventBaseTags = eventEntityTagEntityService.getEventBaseTags(byEventId.getId());
    List<GenericTagEntity> eventGenericTags = eventEntityTagEntityService.getEventGenericTags(byEventId.getId());
    Optional<SubjectTagEntity> eventSubjectTags = eventEntityTagEntityService.getEventSubjectTags(byEventId.getId());

    List<BaseTagDto> baseTagDtoList = eventBaseTags.stream().map(BaseTagEntity::convertEntityToDto).toList();
    List<GenericTagDto> genericTagDtoList = eventGenericTags.stream().map(GenericTagEntity::convertEntityToDto).toList();
    List<SubjectTagDto> subjectTagDtoList = eventSubjectTags.stream().map(SubjectTagEntity::convertEntityToDto).toList();

    List<BaseTag> baseTagsCast = baseTagDtoList.stream().map(BaseTag.class::cast).toList();
    List<BaseTag> genericTagCast = genericTagDtoList.stream().map(BaseTag.class::cast).toList();
    List<BaseTag> subjectTagCast = subjectTagDtoList.stream().map(BaseTag.class::cast).toList();

    List<BaseTag> list = Stream.of(baseTagsCast, genericTagCast, subjectTagCast).flatMap(Collection::stream).toList();

    EventDto eventDto = byEventId.convertEntityToDto();
    eventDto.setTags(list);
    return eventDto;
  }

  public List<EventEntity> getAll() {
    return eventEntityRepository.findAll();
  }
}
