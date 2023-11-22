package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.model.Event;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {
  @Override
  public String processMessage(Event event) {
    return event.content();
  }
}
