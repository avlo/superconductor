package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.model.dto.Nip001Dto;
import com.prosilion.nostrrelay.model.reponse.Nip001Response;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class Nip001Controller {
  @MessageMapping("/nip001")
  @SendTo("/topic/nip001")
  public Nip001Response publish(Nip001Dto nip001Dto) throws Exception {
    return new Nip001Response(String.format("Received Nip001 payload: [%s]", HtmlUtils.htmlEscape(nip001Dto.nip001Field())));
  }
}
