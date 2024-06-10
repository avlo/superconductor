package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.entity.standard.EventTagEntity;
import lombok.NonNull;
import nostr.event.tag.EventTag;

public class EventTagDto implements AbstractTagDto {
  private final EventTag eventTag;

  public EventTagDto(@NonNull EventTag eventTag) {
    this.eventTag = eventTag;
  }

  @Override
  public String getCode() {
    return eventTag.getCode();
  }

  @Override
  public EventTagEntity convertDtoToEntity() {
    return new EventTagEntity(eventTag);
  }
}
