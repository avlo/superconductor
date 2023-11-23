package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.model.Event;

public interface EventService {
  String processIncoming(Event event);
}
