package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.SubjectTagDto;
import com.prosilion.superconductor.service.event.join.EventEntityBaseTagEntityService;
import com.prosilion.superconductor.service.event.join.EventEntityGenericTagEntityService;
import com.prosilion.superconductor.service.event.join.EventEntitySubjectTagEntityService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.SubjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    List<BaseTag> baseTagsOnly = event.getTags().stream()
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
    eventEntityBaseTagEntityService.saveBaseTags(baseTagsOnly, id);

    List<BaseTag> remainingSingleLetterGenericTags = event.getTags().stream()
        .filter(baseTag -> (baseTag.getCode().length() == 1))
        .filter(baseTag -> !List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
    eventEntityGenericTagEntityService.saveGenericTags(remainingSingleLetterGenericTags);

    event.getTags().stream()
        .filter(baseTag -> "subject".equals(baseTag.getCode()))
        .findFirst()
        .ifPresent(baseTag -> eventEntitySubjectTagEntityService.saveSubjectTag(
            new SubjectTagDto(((SubjectTag) baseTag).getSubject()), id));

//    TODO: relay tags?  amount tags?  etc?
  }
}
