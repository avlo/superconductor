package com.prosilion.superconductor.lib.jpa.dto.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.lib.jpa.entity.standard.EventTagJpaEntity;

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
  public EventTagJpaEntity convertDtoToJpaEntity() {
    return new EventTagJpaEntity(eventTag);
  }
}
