package com.prosilion.nostrrelay.config;

import com.prosilion.nostrrelay.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
@Configuration
public class JpaConfig {
  private final EventRepository eventRepository;

  @Autowired
  public JpaConfig(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  EventRepository getEventRepository() {
    return eventRepository;
  }
}
