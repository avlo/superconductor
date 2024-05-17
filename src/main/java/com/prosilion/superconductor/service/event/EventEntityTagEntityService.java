package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.service.event.join.EventEntityBaseTagEntityService;
import com.prosilion.superconductor.service.event.join.EventEntityGenericTagEntityService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Getter
@Service
public class EventEntityTagEntityService {
  private final EventEntityBaseTagEntityService eventEntityBaseTagEntityService;
  private final EventEntityGenericTagEntityService eventEntityGenericTagEntityService;

  @Autowired
  public EventEntityTagEntityService(
      EventEntityBaseTagEntityService eventEntityBaseTagEntityService,
      EventEntityGenericTagEntityService eventEntityGenericTagEntityService) {
    this.eventEntityBaseTagEntityService = eventEntityBaseTagEntityService;
    this.eventEntityGenericTagEntityService = eventEntityGenericTagEntityService;
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
  }
}
