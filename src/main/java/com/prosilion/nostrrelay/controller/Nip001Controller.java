package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.model.dto.Nip001Dto;
import com.prosilion.nostrrelay.model.reponse.Nip001Response;
import com.prosilion.nostrrelay.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class Nip001Controller {
  private final MessageService messageService;

  public Nip001Controller(MessageService messageService) {
    this.messageService = messageService;
  }

  @MessageMapping("/nip001")
  @SendTo("/topic/nip001")
  public Nip001Response publish(Nip001Dto nip001Dto) {
    return new Nip001Response(String.format("Received Nip001 payload: [%s]", HtmlUtils.htmlEscape(messageService.processMessage(nip001Dto))));
  }
}
