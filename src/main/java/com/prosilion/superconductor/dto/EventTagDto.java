package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.EventStandardTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.tag.EventTag;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
public class EventTagDto extends EventTag {
  private String key;

  public EventTagDto(String id) {
    super(id);
  }

  public EventStandardTagEntity convertDtoToEntity() {
    EventStandardTagEntity eventStandardTagEntity = new EventStandardTagEntity();
    BeanUtils.copyProperties(this, eventStandardTagEntity);
    return eventStandardTagEntity;
  }
}
