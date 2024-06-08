package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import com.prosilion.superconductor.service.event.join.EventEntitySubjectTagEntityService;
import com.prosilion.superconductor.service.event.join.generic.EventEntityGenericTagEntityService;
import com.prosilion.superconductor.service.event.join.standard.EventEntityStandardTagEntityService;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventEntityTagEntitiesService {
  private final EventEntityStandardTagEntityService<EventEntityStandardTagEntity, StandardTagEntity> eventEntityStandardTagEntityService;
  private final EventEntityGenericTagEntityService<EventEntityGenericTagEntity, GenericTagEntity> eventEntityGenericTagEntityService;
  private final EventEntitySubjectTagEntityService eventEntitySubjectTagEntityService;

  @Autowired
  public EventEntityTagEntitiesService(
      EventEntityStandardTagEntityService<EventEntityStandardTagEntity, StandardTagEntity> eventEntityStandardTagEntityService,
      EventEntityGenericTagEntityService<EventEntityGenericTagEntity, GenericTagEntity> eventEntityGenericTagEntityService,
      EventEntitySubjectTagEntityService eventEntitySubjectTagEntityService) {
    this.eventEntityStandardTagEntityService = eventEntityStandardTagEntityService;
    this.eventEntityGenericTagEntityService = eventEntityGenericTagEntityService;
    this.eventEntitySubjectTagEntityService = eventEntitySubjectTagEntityService;
  }

  public void saveTags(GenericEvent event, Long id) {
    eventEntityStandardTagEntityService.saveStandardTags(event, id);
    eventEntityGenericTagEntityService.saveGenericTags(event);
    eventEntitySubjectTagEntityService.saveSubjectTag(event, id);
//    TODO: relay tags?  amount tags?  etc?
  }

  public List<StandardTagEntity> getEventStandardTags(Long eventId) {
    return eventEntityStandardTagEntityService.getTags(eventId);
  }

  public List<GenericTagEntity> getEventGenericTags(Long eventId) {
    return eventEntityGenericTagEntityService.getTags(eventId);
  }

  public Optional<SubjectTagEntity> getEventSubjectTag(Long eventId) {
    return eventEntitySubjectTagEntityService.getTags(eventId);
  }
}