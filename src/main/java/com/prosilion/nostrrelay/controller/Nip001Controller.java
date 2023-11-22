package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.model.Event;
import com.prosilion.nostrrelay.model.reponse.Nip001Response;
import com.prosilion.nostrrelay.service.EventService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class Nip001Controller {
  private final EventService eventService;

  public Nip001Controller(EventService eventService) {
    this.eventService = eventService;
  }

  @MessageMapping("/nip001")
  @SendTo("/topic/nip001")
  public Nip001Response publish(Event event) {
    return new Nip001Response(String.format("Received Nip001 payload: [%s]", HtmlUtils.htmlEscape(eventService.processMessage(event))));
  }
}
