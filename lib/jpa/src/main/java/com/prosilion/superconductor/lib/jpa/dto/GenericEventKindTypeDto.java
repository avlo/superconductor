package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindType;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindTypeIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;

public record GenericEventKindTypeDto(BaseEvent event, KindTypeIF kindType) {

  public EventEntityIF convertDtoToEntity() {
    return new GenericEventKindDto(event).convertDtoToEntity();
  }

  public GenericEventKindTypeIF convertBaseEventToEventIF() {
    return new GenericEventKindType(
        new GenericEventKindDto(event).convertBaseEventToEventIF(),
        kindType);
  }
}
