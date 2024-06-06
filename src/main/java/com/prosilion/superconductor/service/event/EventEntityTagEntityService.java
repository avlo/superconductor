package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.entity.BaseTagEntity;
import com.prosilion.superconductor.entity.SubjectTagEntity;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.service.event.join.EventEntityBaseTagEntityService;
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
public class EventEntityTagEntityService {
  private final EventEntityBaseTagEntityService eventEntityBaseTagEntityService;
  private final EventEntityGenericTagEntityService eventEntityGenericTagEntityService;
  private final EventEntitySubjectTagEntityService eventEntitySubjectTagEntityService;

  @Autowired
  public EventEntityTagEntityService(
      EventEntityBaseTagEntityService eventEntityBaseTagEntityService,
      EventEntityGenericTagEntityService eventEntityGenericTagEntityService,
      EventEntitySubjectTagEntityService eventEntitySubjectTagEntityService) {
    this.eventEntityBaseTagEntityService = eventEntityBaseTagEntityService;
    this.eventEntityGenericTagEntityService = eventEntityGenericTagEntityService;
    this.eventEntitySubjectTagEntityService = eventEntitySubjectTagEntityService;
  }

  public void saveTags(GenericEvent event, Long id) {
    eventEntityBaseTagEntityService.saveBaseTags(event, id);
    eventEntityGenericTagEntityService.saveGenericTags(event);
    eventEntitySubjectTagEntityService.saveSubjectTag(event, id);
//    TODO: relay tags?  amount tags?  etc?
  }

  public List<BaseTagEntity> getEventBaseTags(Long eventId) {
    return eventEntityBaseTagEntityService.getTags(eventId);
  }

  public <T extends GenericTagEntity> List<T> getEventGenericTags(Long eventId) {
    return eventEntityGenericTagEntityService.getTags(eventId);
  }

  public Optional<SubjectTagEntity> getEventSubjectTags(Long eventId) {
    return eventEntitySubjectTagEntityService.getTags(eventId);
  }
}