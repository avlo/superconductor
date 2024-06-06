package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.entity.EventStandardTagEntity;
import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.service.event.join.EventEntityEventStandardTagEntityService;
import com.prosilion.superconductor.service.event.join.EventEntityGenericTagEntityService;
import com.prosilion.superconductor.service.event.join.EventEntitySubjectTagEntityService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Service
public class EventEntityTagEntitiesService {
  private final EventEntityEventStandardTagEntityService eventEntityEventStandardTagEntityService;
  private final EventEntityGenericTagEntityService eventEntityGenericTagEntityService;
  private final EventEntitySubjectTagEntityService eventEntitySubjectTagEntityService;

  @Autowired
  public EventEntityTagEntitiesService(
      EventEntityEventStandardTagEntityService eventEntityEventStandardTagEntityService,
      EventEntityGenericTagEntityService eventEntityGenericTagEntityService,
      EventEntitySubjectTagEntityService eventEntitySubjectTagEntityService) {
    this.eventEntityEventStandardTagEntityService = eventEntityEventStandardTagEntityService;
    this.eventEntityGenericTagEntityService = eventEntityGenericTagEntityService;
    this.eventEntitySubjectTagEntityService = eventEntitySubjectTagEntityService;
  }

  public void saveTags(GenericEvent event, Long id) {
    eventEntityEventStandardTagEntityService.saveEventStandardTags(event, id);
    eventEntityGenericTagEntityService.saveGenericTags(event);
    eventEntitySubjectTagEntityService.saveSubjectTag(event, id);
//    TODO: relay tags?  amount tags?  etc?
  }

  public List<EventStandardTagEntity> getEventStandardTags(Long eventId) {
    return eventEntityEventStandardTagEntityService.getTags(eventId);
  }

  public <T extends GenericTagEntity> List<T> getEventGenericTags(Long eventId) {
    return eventEntityGenericTagEntityService.getTags(eventId);
  }

  public Optional<SubjectTagEntity> getEventSubjectTag(Long eventId) {
    return eventEntitySubjectTagEntityService.getTags(eventId);
  }
}