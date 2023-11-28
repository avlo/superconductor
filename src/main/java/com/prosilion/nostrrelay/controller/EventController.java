package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.model.Event;
import com.prosilion.nostrrelay.service.EventService;
import com.prosilion.nostrrelay.view.Response;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class EventController {
  private final EventService eventService;

  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

//  @MessageMapping("/topic_001")
//  @SendTo("/topic/topic_001")
  @MessageMapping("/")
  @SendTo("/")
  public Response publish(Event event) {
    return new Response(String.format("Received Nip001 payload: [%s]", HtmlUtils.htmlEscape(eventService.processIncoming(event))));
  }
}
