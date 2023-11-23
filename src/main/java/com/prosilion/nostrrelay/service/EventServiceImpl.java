package com.prosilion.nostrrelay.service;

import com.google.gson.Gson;
import com.prosilion.nostrrelay.model.Event;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {
  @Override
  public String processIncoming(Event event) {
    return new Gson().toJson(event);
  }
}
