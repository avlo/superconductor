package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.model.Event;

public interface EventService {
  String processMessage(Event event);
}
