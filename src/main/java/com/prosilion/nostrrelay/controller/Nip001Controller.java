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
	public Nip001Response greeting(Nip001Dto message) throws Exception {
		Thread.sleep(1000); // simulated delay
		return new Nip001Response("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

}
