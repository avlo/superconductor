package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.event.EventDto;
public interface EventEntityIf {
  EventDto convertEntityToDto();
}
