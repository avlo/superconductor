package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.standard.EventTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.tag.EventTag;

@Setter
public class EventTagDto extends StandardTagDto implements StandardTagDtoIF {
  private final EventTag eventTag;

  public EventTagDto(@NonNull EventTag eventTag) {
    this.eventTag = eventTag;
  }

  @Override
  public Character getCode() {
    return eventTag.getCode().charAt(0);
  }

  @Override
  public <T extends StandardTagEntity> T convertDtoToEntity() {
    return (T) new EventTagEntity(eventTag);
  }
}
