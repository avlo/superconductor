package com.prosilion.superconductor.base.dto.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.entity.standard.EventTagEntity;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.EventTag;

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
